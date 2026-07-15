package com.rrhh.dashboard.Asistencia.services;

import com.rrhh.dashboard.Asistencia.Entity.AttendanceRecord;
import com.rrhh.dashboard.Asistencia.Entity.EstadoVerificacionFacial;
import com.rrhh.dashboard.Asistencia.Repository.AttendanceRecordRepository;
import com.rrhh.dashboard.Asistencia.exceptions.AlreadyClockedInException;
import com.rrhh.dashboard.Asistencia.exceptions.AttendanceRecordNotFoundException;
import com.rrhh.dashboard.Asistencia.exceptions.ForbiddenAttendanceAccessException;
import com.rrhh.dashboard.Asistencia.exceptions.NoOpenAttendanceRecordException;
import com.rrhh.dashboard.Empleados.Entity.Empleados;
import com.rrhh.dashboard.Empleados.Repository.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Registro de asistencia por autorreporte del propio empleado: "Entrada" y
 * "Salida", fecha/hora capturada automaticamente por el servidor.
 *
 * <p>Un Empleado solo puede fichar su propia entrada/salida, no la de
 * otros — se resuelve aca comparando {@code employeeId} con el empleado
 * autenticado, no solo con {@code @PreAuthorize} (que solo valida rol).</p>
 */
@Service
public class AttendanceService {

    private final AttendanceRecordRepository repository;
    private final EmpleadoRepository empleadoRepository;
    private final ReconocimientoFacialClient reconocimientoFacialClient;
    private final double umbralAuto;

    public AttendanceService(AttendanceRecordRepository repository,
                              EmpleadoRepository empleadoRepository,
                              ReconocimientoFacialClient reconocimientoFacialClient,
                              @Value("${app.reconocimiento-facial.umbral-auto}") double umbralAuto) {
        this.repository = repository;
        this.empleadoRepository = empleadoRepository;
        this.reconocimientoFacialClient = reconocimientoFacialClient;
        this.umbralAuto = umbralAuto;
    }

    /**
     * Ficha la Entrada y, si viene una foto, corre la verificacion facial
     * contra la foto de referencia del empleado. La entrada se registra
     * SIEMPRE — sin foto de referencia enrolada, sin foto capturada, con
     * baja similitud, o si el servicio de reconocimiento no responde, el
     * unico efecto es que queda PENDIENTE_REVISION en vez de
     * VERIFICADO_AUTOMATICO; nunca se rechaza el fichaje por esto.
     */
    @Transactional
    public AttendanceRecord clockIn(Long employeeId, Long authenticatedEmployeeId, byte[] fotoCapturada) {
        requireOwnRecord(employeeId, authenticatedEmployeeId);
        if (repository.existsByEmployeeIdAndClockOutAtIsNull(employeeId)) {
            throw new AlreadyClockedInException(employeeId);
        }

        AttendanceRecord record = new AttendanceRecord(employeeId);
        if (fotoCapturada != null && fotoCapturada.length > 0) {
            record.registrarVerificacionFacial(fotoCapturada, null, EstadoVerificacionFacial.PENDIENTE_REVISION);
            aplicarVerificacionFacial(record, employeeId, fotoCapturada);
        }
        return repository.save(record);
    }

    private void aplicarVerificacionFacial(AttendanceRecord record, Long employeeId, byte[] fotoCapturada) {
        byte[] fotoReferencia = empleadoRepository.findById(employeeId)
                .map(Empleados::getFotoReferencia)
                .orElse(null);
        if (fotoReferencia == null || fotoReferencia.length == 0) {
            return; // sigue PENDIENTE_REVISION: no hay contra que comparar
        }

        Optional<ResultadoComparacionFacial> resultado = reconocimientoFacialClient.comparar(fotoReferencia, fotoCapturada);
        if (resultado.isEmpty() || !resultado.get().seDetectaronAmbosRostros()) {
            return; // sigue PENDIENTE_REVISION: fallo el servicio o no se detecto rostro
        }

        double similitud = resultado.get().similitud();
        EstadoVerificacionFacial estado = similitud >= umbralAuto
                ? EstadoVerificacionFacial.VERIFICADO_AUTOMATICO
                : EstadoVerificacionFacial.PENDIENTE_REVISION;
        record.registrarVerificacionFacial(fotoCapturada, similitud, estado);
    }

    @Transactional
    public AttendanceRecord clockOut(Long employeeId, Long authenticatedEmployeeId) {
        requireOwnRecord(employeeId, authenticatedEmployeeId);
        AttendanceRecord record = repository
                .findFirstByEmployeeIdAndClockOutAtIsNullOrderByClockInAtDesc(employeeId)
                .orElseThrow(() -> new NoOpenAttendanceRecordException(employeeId));
        record.registerClockOut();
        return record;
    }

    private void requireOwnRecord(Long employeeId, Long authenticatedEmployeeId) {
        if (!employeeId.equals(authenticatedEmployeeId)) {
            throw new ForbiddenAttendanceAccessException(employeeId, authenticatedEmployeeId);
        }
    }

    @Transactional(readOnly = true)
    public List<AttendanceRecord> listByEmployee(Long employeeId) {
        return repository.findAllByEmployeeId(employeeId);
    }

    @Transactional(readOnly = true)
    public AttendanceRecord obtenerPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new AttendanceRecordNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public byte[] obtenerFotoFichaje(Long id) {
        return repository.findById(id)
                .map(AttendanceRecord::getFotoCapturada)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<AttendanceRecord> listarPendientesDeRevision() {
        return repository.findByEstadoVerificacion(EstadoVerificacionFacial.PENDIENTE_REVISION);
    }

    @Transactional
    public AttendanceRecord revisar(Long id, boolean aprobado) {
        AttendanceRecord record = obtenerPorId(id);
        record.revisarManualmente(aprobado);
        return repository.save(record);
    }

    @Transactional(readOnly = true)
    public List<AttendanceRecord> listAll() {
        return repository.findAll();
    }
}

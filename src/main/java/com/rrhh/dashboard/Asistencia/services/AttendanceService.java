package com.rrhh.dashboard.Asistencia.services;

import com.rrhh.dashboard.Asistencia.Entity.AttendanceRecord;
import com.rrhh.dashboard.Asistencia.Repository.AttendanceRecordRepository;
import com.rrhh.dashboard.Asistencia.exceptions.AlreadyClockedInException;
import com.rrhh.dashboard.Asistencia.exceptions.ForbiddenAttendanceAccessException;
import com.rrhh.dashboard.Asistencia.exceptions.NoOpenAttendanceRecordException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public AttendanceService(AttendanceRecordRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public AttendanceRecord clockIn(Long employeeId, Long authenticatedEmployeeId) {
        requireOwnRecord(employeeId, authenticatedEmployeeId);
        if (repository.existsByEmployeeIdAndClockOutAtIsNull(employeeId)) {
            throw new AlreadyClockedInException(employeeId);
        }
        return repository.save(new AttendanceRecord(employeeId));
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
    public List<AttendanceRecord> listAll() {
        return repository.findAll();
    }
}

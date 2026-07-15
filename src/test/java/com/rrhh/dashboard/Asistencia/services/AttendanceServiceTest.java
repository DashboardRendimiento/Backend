package com.rrhh.dashboard.Asistencia.services;

import com.rrhh.dashboard.Asistencia.Entity.AttendanceRecord;
import com.rrhh.dashboard.Asistencia.Entity.EstadoVerificacionFacial;
import com.rrhh.dashboard.Asistencia.Repository.AttendanceRecordRepository;
import com.rrhh.dashboard.Asistencia.exceptions.AlreadyClockedInException;
import com.rrhh.dashboard.Asistencia.exceptions.AlreadyClockedOutException;
import com.rrhh.dashboard.Asistencia.exceptions.AttendanceRecordNotFoundException;
import com.rrhh.dashboard.Asistencia.exceptions.ForbiddenAttendanceAccessException;
import com.rrhh.dashboard.Asistencia.exceptions.NoOpenAttendanceRecordException;
import com.rrhh.dashboard.Asistencia.exceptions.RevisionNoAplicableException;
import com.rrhh.dashboard.Empleados.Entity.Empleados;
import com.rrhh.dashboard.Empleados.Repository.EmpleadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    private static final double UMBRAL_AUTO = 0.75;

    @Mock
    private AttendanceRecordRepository repository;

    @Mock
    private EmpleadoRepository empleadoRepository;

    @Mock
    private ReconocimientoFacialClient reconocimientoFacialClient;

    private AttendanceService service;

    @BeforeEach
    void setUp() {
        service = new AttendanceService(repository, empleadoRepository, reconocimientoFacialClient, UMBRAL_AUTO);
    }

    @Test
    void clockInSinFotoQuedaSinVerificar() {
        Long employeeId = 1L;
        when(repository.existsByEmployeeIdAndClockOutAtIsNull(employeeId)).thenReturn(false);
        when(repository.save(any(AttendanceRecord.class))).thenAnswer(inv -> inv.getArgument(0));

        AttendanceRecord result = service.clockIn(employeeId, employeeId, null);

        assertThat(result.getEmployeeId()).isEqualTo(employeeId);
        assertThat(result.isOpen()).isTrue();
        assertThat(result.getEstadoVerificacion()).isNull();
        verify(empleadoRepository, never()).findById(any());
    }

    @Test
    void clockInFailsWhenAlreadyClockedIn() {
        Long employeeId = 1L;
        when(repository.existsByEmployeeIdAndClockOutAtIsNull(employeeId)).thenReturn(true);

        assertThatThrownBy(() -> service.clockIn(employeeId, employeeId, null))
                .isInstanceOf(AlreadyClockedInException.class);
    }

    @Test
    void clockInFailsWhenAuthenticatedEmployeeIsNotTheTargetEmployee() {
        Long employeeId = 1L;
        Long authenticatedEmployeeId = 2L;

        assertThatThrownBy(() -> service.clockIn(employeeId, authenticatedEmployeeId, null))
                .isInstanceOf(ForbiddenAttendanceAccessException.class);

        verify(repository, never()).existsByEmployeeIdAndClockOutAtIsNull(any());
    }

    @Test
    void clockInConFotoPeroSinReferenciaQuedaPendiente() {
        Long employeeId = 1L;
        when(repository.existsByEmployeeIdAndClockOutAtIsNull(employeeId)).thenReturn(false);
        when(repository.save(any(AttendanceRecord.class))).thenAnswer(inv -> inv.getArgument(0));
        when(empleadoRepository.findById(employeeId)).thenReturn(Optional.of(new Empleados()));

        AttendanceRecord result = service.clockIn(employeeId, employeeId, new byte[]{1, 2, 3});

        assertThat(result.getEstadoVerificacion()).isEqualTo(EstadoVerificacionFacial.PENDIENTE_REVISION);
        verify(reconocimientoFacialClient, never()).comparar(any(), any());
    }

    @Test
    void clockInConSimilitudAltaQuedaVerificadoAutomatico() {
        Long employeeId = 1L;
        Empleados empleado = new Empleados();
        empleado.setFotoReferencia(new byte[]{9, 9, 9});
        when(repository.existsByEmployeeIdAndClockOutAtIsNull(employeeId)).thenReturn(false);
        when(repository.save(any(AttendanceRecord.class))).thenAnswer(inv -> inv.getArgument(0));
        when(empleadoRepository.findById(employeeId)).thenReturn(Optional.of(empleado));
        when(reconocimientoFacialClient.comparar(any(), any()))
                .thenReturn(Optional.of(new ResultadoComparacionFacial(true, true, 0.9, 12.0)));

        AttendanceRecord result = service.clockIn(employeeId, employeeId, new byte[]{1, 2, 3});

        assertThat(result.getEstadoVerificacion()).isEqualTo(EstadoVerificacionFacial.VERIFICADO_AUTOMATICO);
        assertThat(result.getSimilitudFacial()).isEqualTo(0.9);
    }

    @Test
    void clockInConSimilitudBajaQuedaPendiente() {
        Long employeeId = 1L;
        Empleados empleado = new Empleados();
        empleado.setFotoReferencia(new byte[]{9, 9, 9});
        when(repository.existsByEmployeeIdAndClockOutAtIsNull(employeeId)).thenReturn(false);
        when(repository.save(any(AttendanceRecord.class))).thenAnswer(inv -> inv.getArgument(0));
        when(empleadoRepository.findById(employeeId)).thenReturn(Optional.of(empleado));
        when(reconocimientoFacialClient.comparar(any(), any()))
                .thenReturn(Optional.of(new ResultadoComparacionFacial(true, true, 0.4, 60.0)));

        AttendanceRecord result = service.clockIn(employeeId, employeeId, new byte[]{1, 2, 3});

        assertThat(result.getEstadoVerificacion()).isEqualTo(EstadoVerificacionFacial.PENDIENTE_REVISION);
    }

    @Test
    void clockInQuedaPendienteSiNoSeDetectaAlgunRostro() {
        Long employeeId = 1L;
        Empleados empleado = new Empleados();
        empleado.setFotoReferencia(new byte[]{9, 9, 9});
        when(repository.existsByEmployeeIdAndClockOutAtIsNull(employeeId)).thenReturn(false);
        when(repository.save(any(AttendanceRecord.class))).thenAnswer(inv -> inv.getArgument(0));
        when(empleadoRepository.findById(employeeId)).thenReturn(Optional.of(empleado));
        when(reconocimientoFacialClient.comparar(any(), any()))
                .thenReturn(Optional.of(new ResultadoComparacionFacial(true, false, 0.0, null)));

        AttendanceRecord result = service.clockIn(employeeId, employeeId, new byte[]{1, 2, 3});

        assertThat(result.getEstadoVerificacion()).isEqualTo(EstadoVerificacionFacial.PENDIENTE_REVISION);
    }

    @Test
    void clockInQuedaPendienteSiElServicioDeReconocimientoFalla() {
        Long employeeId = 1L;
        Empleados empleado = new Empleados();
        empleado.setFotoReferencia(new byte[]{9, 9, 9});
        when(repository.existsByEmployeeIdAndClockOutAtIsNull(employeeId)).thenReturn(false);
        when(repository.save(any(AttendanceRecord.class))).thenAnswer(inv -> inv.getArgument(0));
        when(empleadoRepository.findById(employeeId)).thenReturn(Optional.of(empleado));
        when(reconocimientoFacialClient.comparar(any(), any())).thenReturn(Optional.empty());

        AttendanceRecord result = service.clockIn(employeeId, employeeId, new byte[]{1, 2, 3});

        assertThat(result.getEstadoVerificacion()).isEqualTo(EstadoVerificacionFacial.PENDIENTE_REVISION);
    }

    @Test
    void clockOutClosesOpenRecord() {
        Long employeeId = 1L;
        AttendanceRecord openRecord = new AttendanceRecord(employeeId);
        when(repository.findFirstByEmployeeIdAndClockOutAtIsNullOrderByClockInAtDesc(employeeId))
                .thenReturn(Optional.of(openRecord));

        AttendanceRecord result = service.clockOut(employeeId, employeeId);

        assertThat(result.getClockOutAt()).isNotNull();
        assertThat(result.isOpen()).isFalse();
    }

    @Test
    void clockOutFailsWhenNoOpenRecord() {
        Long employeeId = 1L;
        when(repository.findFirstByEmployeeIdAndClockOutAtIsNullOrderByClockInAtDesc(employeeId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.clockOut(employeeId, employeeId))
                .isInstanceOf(NoOpenAttendanceRecordException.class);
    }

    @Test
    void clockOutFailsWhenRecordAlreadyClosed() {
        Long employeeId = 1L;
        AttendanceRecord closedRecord = new AttendanceRecord(employeeId);
        closedRecord.registerClockOut();
        when(repository.findFirstByEmployeeIdAndClockOutAtIsNullOrderByClockInAtDesc(employeeId))
                .thenReturn(Optional.of(closedRecord));

        assertThatThrownBy(() -> service.clockOut(employeeId, employeeId))
                .isInstanceOf(AlreadyClockedOutException.class);
    }

    @Test
    void clockOutFailsWhenAuthenticatedEmployeeIsNotTheTargetEmployee() {
        Long employeeId = 1L;
        Long authenticatedEmployeeId = 2L;

        assertThatThrownBy(() -> service.clockOut(employeeId, authenticatedEmployeeId))
                .isInstanceOf(ForbiddenAttendanceAccessException.class);

        verify(repository, never()).findFirstByEmployeeIdAndClockOutAtIsNullOrderByClockInAtDesc(any());
    }

    @Test
    void listByEmployeeDelegatesToRepository() {
        Long employeeId = 1L;
        when(repository.findAllByEmployeeId(employeeId)).thenReturn(List.of(new AttendanceRecord(employeeId)));

        List<AttendanceRecord> result = service.listByEmployee(employeeId);

        assertThat(result).hasSize(1);
    }

    @Test
    void revisarApruebaUnFichajePendiente() {
        AttendanceRecord record = new AttendanceRecord(1L);
        record.registrarVerificacionFacial(new byte[]{1}, 0.5, EstadoVerificacionFacial.PENDIENTE_REVISION);
        when(repository.findById(10L)).thenReturn(Optional.of(record));

        AttendanceRecord result = service.revisar(10L, true);

        assertThat(result.getEstadoVerificacion()).isEqualTo(EstadoVerificacionFacial.VERIFICADO_MANUAL);
    }

    @Test
    void revisarRechazaUnFichajePendiente() {
        AttendanceRecord record = new AttendanceRecord(1L);
        record.registrarVerificacionFacial(new byte[]{1}, 0.5, EstadoVerificacionFacial.PENDIENTE_REVISION);
        when(repository.findById(10L)).thenReturn(Optional.of(record));

        AttendanceRecord result = service.revisar(10L, false);

        assertThat(result.getEstadoVerificacion()).isEqualTo(EstadoVerificacionFacial.RECHAZADO);
    }

    @Test
    void revisarFallaSiElFichajeNoEstaPendiente() {
        AttendanceRecord record = new AttendanceRecord(1L);
        record.registrarVerificacionFacial(new byte[]{1}, 0.9, EstadoVerificacionFacial.VERIFICADO_AUTOMATICO);
        when(repository.findById(10L)).thenReturn(Optional.of(record));

        assertThatThrownBy(() -> service.revisar(10L, true))
                .isInstanceOf(RevisionNoAplicableException.class);
    }

    @Test
    void revisarFallaSiElFichajeNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.revisar(99L, true))
                .isInstanceOf(AttendanceRecordNotFoundException.class);
    }
}

package com.rrhh.dashboard.Asistencia.services;

import com.rrhh.dashboard.Asistencia.Entity.AttendanceRecord;
import com.rrhh.dashboard.Asistencia.Repository.AttendanceRecordRepository;
import com.rrhh.dashboard.Asistencia.exceptions.AlreadyClockedInException;
import com.rrhh.dashboard.Asistencia.exceptions.AlreadyClockedOutException;
import com.rrhh.dashboard.Asistencia.exceptions.ForbiddenAttendanceAccessException;
import com.rrhh.dashboard.Asistencia.exceptions.NoOpenAttendanceRecordException;
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

    @Mock
    private AttendanceRecordRepository repository;

    private AttendanceService service;

    @BeforeEach
    void setUp() {
        service = new AttendanceService(repository);
    }

    @Test
    void clockInCreatesOpenRecord() {
        Long employeeId = 1L;
        when(repository.existsByEmployeeIdAndClockOutAtIsNull(employeeId)).thenReturn(false);
        when(repository.save(any(AttendanceRecord.class))).thenAnswer(inv -> inv.getArgument(0));

        AttendanceRecord result = service.clockIn(employeeId, employeeId);

        assertThat(result.getEmployeeId()).isEqualTo(employeeId);
        assertThat(result.getClockInAt()).isNotNull();
        assertThat(result.isOpen()).isTrue();
    }

    @Test
    void clockInFailsWhenAlreadyClockedIn() {
        Long employeeId = 1L;
        when(repository.existsByEmployeeIdAndClockOutAtIsNull(employeeId)).thenReturn(true);

        assertThatThrownBy(() -> service.clockIn(employeeId, employeeId))
                .isInstanceOf(AlreadyClockedInException.class);
    }

    @Test
    void clockInFailsWhenAuthenticatedEmployeeIsNotTheTargetEmployee() {
        Long employeeId = 1L;
        Long authenticatedEmployeeId = 2L;

        assertThatThrownBy(() -> service.clockIn(employeeId, authenticatedEmployeeId))
                .isInstanceOf(ForbiddenAttendanceAccessException.class);

        verify(repository, never()).existsByEmployeeIdAndClockOutAtIsNull(any());
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
}

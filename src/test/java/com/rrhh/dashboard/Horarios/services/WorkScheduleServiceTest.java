package com.rrhh.dashboard.Horarios.services;

import com.rrhh.dashboard.Horarios.Entity.WorkSchedule;
import com.rrhh.dashboard.Horarios.Repository.WorkScheduleRepository;
import com.rrhh.dashboard.Horarios.exceptions.DuplicateScheduleException;
import com.rrhh.dashboard.Horarios.exceptions.EmptyWorkDaysException;
import com.rrhh.dashboard.Horarios.exceptions.InvalidScheduleTimeRangeException;
import com.rrhh.dashboard.Horarios.exceptions.WorkScheduleNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkScheduleServiceTest {

    @Mock
    private WorkScheduleRepository repository;

    private WorkScheduleService service;

    private static final Set<DayOfWeek> WEEKDAYS =
            EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);

    @BeforeEach
    void setUp() {
        service = new WorkScheduleService(repository);
    }

    @Test
    void assignCreatesScheduleWhenEmployeeHasNone() {
        Long employeeId = 1L;
        when(repository.existsByEmployeeId(employeeId)).thenReturn(false);
        when(repository.save(any(WorkSchedule.class))).thenAnswer(inv -> inv.getArgument(0));

        WorkSchedule result = service.assign(employeeId, WEEKDAYS, LocalTime.of(9, 0), LocalTime.of(18, 0));

        assertThat(result.getEmployeeId()).isEqualTo(employeeId);
        assertThat(result.getWorkDays()).isEqualTo(WEEKDAYS);
        assertThat(result.getStartTime()).isEqualTo(LocalTime.of(9, 0));
        assertThat(result.getEndTime()).isEqualTo(LocalTime.of(18, 0));
    }

    @Test
    void assignFailsWhenEmployeeAlreadyHasSchedule() {
        Long employeeId = 1L;
        when(repository.existsByEmployeeId(employeeId)).thenReturn(true);

        assertThatThrownBy(() -> service.assign(employeeId, WEEKDAYS, LocalTime.of(9, 0), LocalTime.of(18, 0)))
                .isInstanceOf(DuplicateScheduleException.class);

        verify(repository, never()).save(any(WorkSchedule.class));
    }

    @Test
    void assignFailsWithEmptyWorkDays() {
        Long employeeId = 1L;
        when(repository.existsByEmployeeId(employeeId)).thenReturn(false);

        assertThatThrownBy(() -> service.assign(employeeId, Set.of(), LocalTime.of(9, 0), LocalTime.of(18, 0)))
                .isInstanceOf(EmptyWorkDaysException.class);
    }

    @Test
    void assignFailsWhenEndTimeNotAfterStartTime() {
        Long employeeId = 1L;
        when(repository.existsByEmployeeId(employeeId)).thenReturn(false);

        assertThatThrownBy(() -> service.assign(employeeId, WEEKDAYS, LocalTime.of(18, 0), LocalTime.of(9, 0)))
                .isInstanceOf(InvalidScheduleTimeRangeException.class);
    }

    @Test
    void updateReplacesDaysAndTimes() {
        Long employeeId = 1L;
        WorkSchedule existing = new WorkSchedule(employeeId, WEEKDAYS, LocalTime.of(9, 0), LocalTime.of(18, 0));
        when(repository.findById(any(Long.class))).thenReturn(Optional.of(existing));

        Set<DayOfWeek> newDays = EnumSet.of(DayOfWeek.SATURDAY);
        WorkSchedule result = service.update(2L, newDays, LocalTime.of(8, 0), LocalTime.of(12, 0));

        assertThat(result.getWorkDays()).isEqualTo(newDays);
        assertThat(result.getStartTime()).isEqualTo(LocalTime.of(8, 0));
        assertThat(result.getEndTime()).isEqualTo(LocalTime.of(12, 0));
    }

    @Test
    void updateFailsWhenScheduleDoesNotExist() {
        when(repository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(2L, WEEKDAYS, LocalTime.of(9, 0), LocalTime.of(18, 0)))
                .isInstanceOf(WorkScheduleNotFoundException.class);
    }

    @Test
    void unassignDeletesExistingSchedule() {
        Long id = 1L;
        when(repository.existsById(id)).thenReturn(true);

        service.unassign(id);

        verify(repository, times(1)).deleteById(id);
    }

    @Test
    void unassignFailsWhenScheduleDoesNotExist() {
        Long id = 1L;
        when(repository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> service.unassign(id))
                .isInstanceOf(WorkScheduleNotFoundException.class);

        verify(repository, never()).deleteById(any(Long.class));
    }

    @Test
    void getReturnsScheduleById() {
        Long id = 1L;
        WorkSchedule schedule = new WorkSchedule(2L, WEEKDAYS, LocalTime.of(9, 0), LocalTime.of(18, 0));
        when(repository.findById(id)).thenReturn(Optional.of(schedule));

        assertThat(service.get(id)).isEqualTo(schedule);
    }

    @Test
    void getFailsWhenScheduleDoesNotExist() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(id)).isInstanceOf(WorkScheduleNotFoundException.class);
    }

    @Test
    void getByEmployeeReturnsScheduleForEmployee() {
        Long employeeId = 1L;
        WorkSchedule schedule = new WorkSchedule(employeeId, WEEKDAYS, LocalTime.of(9, 0), LocalTime.of(18, 0));
        when(repository.findByEmployeeId(employeeId)).thenReturn(Optional.of(schedule));

        assertThat(service.getByEmployee(employeeId)).isEqualTo(schedule);
    }

    @Test
    void getByEmployeeFailsWhenEmployeeHasNoSchedule() {
        Long employeeId = 1L;
        when(repository.findByEmployeeId(employeeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByEmployee(employeeId))
                .isInstanceOf(WorkScheduleNotFoundException.class);
    }

    @Test
    void listAllDelegatesToRepository() {
        WorkSchedule schedule = new WorkSchedule(1L, WEEKDAYS, LocalTime.of(9, 0), LocalTime.of(18, 0));
        when(repository.findAll()).thenReturn(List.of(schedule));

        assertThat(service.listAll()).hasSize(1);
    }
}

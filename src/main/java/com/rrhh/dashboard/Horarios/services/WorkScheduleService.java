package com.rrhh.dashboard.Horarios.services;

import com.rrhh.dashboard.Horarios.Entity.WorkSchedule;
import com.rrhh.dashboard.Horarios.Repository.WorkScheduleRepository;
import com.rrhh.dashboard.Horarios.exceptions.DuplicateScheduleException;
import com.rrhh.dashboard.Horarios.exceptions.WorkScheduleNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

/**
 * Asignacion de horario de trabajo a un empleado: el Administrador crea,
 * actualiza y quita el horario asignado a cualquier empleado. La
 * autorizacion por rol (solo ADMINISTRADOR/SUPERADMIN) se resuelve en la
 * capa web via @PreAuthorize.
 */
@Service
public class WorkScheduleService {

    private final WorkScheduleRepository repository;

    public WorkScheduleService(WorkScheduleRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public WorkSchedule assign(Long employeeId, Set<DayOfWeek> workDays, LocalTime startTime, LocalTime endTime) {
        if (repository.existsByEmployeeId(employeeId)) {
            throw new DuplicateScheduleException(employeeId);
        }
        return repository.save(new WorkSchedule(employeeId, workDays, startTime, endTime));
    }

    @Transactional
    public WorkSchedule update(Long id, Set<DayOfWeek> workDays, LocalTime startTime, LocalTime endTime) {
        WorkSchedule schedule = repository.findById(id)
                .orElseThrow(() -> WorkScheduleNotFoundException.byId(id));
        schedule.applyChange(workDays, startTime, endTime);
        return schedule;
    }

    @Transactional
    public void unassign(Long id) {
        if (!repository.existsById(id)) {
            throw WorkScheduleNotFoundException.byId(id);
        }
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public WorkSchedule get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> WorkScheduleNotFoundException.byId(id));
    }

    @Transactional(readOnly = true)
    public WorkSchedule getByEmployee(Long employeeId) {
        return repository.findByEmployeeId(employeeId)
                .orElseThrow(() -> WorkScheduleNotFoundException.forEmployee(employeeId));
    }

    @Transactional(readOnly = true)
    public List<WorkSchedule> listAll() {
        return repository.findAll();
    }
}

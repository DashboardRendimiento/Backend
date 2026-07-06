package com.rrhh.dashboard.Horarios.controllers;

import com.rrhh.dashboard.Horarios.Entity.WorkSchedule;
import com.rrhh.dashboard.Horarios.dtos.AssignWorkScheduleRequest;
import com.rrhh.dashboard.Horarios.dtos.UpdateWorkScheduleRequest;
import com.rrhh.dashboard.Horarios.dtos.WorkScheduleResponse;
import com.rrhh.dashboard.Horarios.services.WorkScheduleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Asignacion de horarios: el Administrador asigna, actualiza y quita el
 * horario de trabajo de los empleados. Escritura y consulta restringidas a
 * ADMINISTRADOR/SUPERADMIN — R-007 no describe ninguna accion del propio
 * Empleado sobre su horario.
 */
@RestController
@RequestMapping("/api/work-schedules")
public class WorkScheduleController {

    private final WorkScheduleService workScheduleService;

    public WorkScheduleController(WorkScheduleService workScheduleService) {
        this.workScheduleService = workScheduleService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPERADMIN')")
    public ResponseEntity<WorkScheduleResponse> assign(@Valid @RequestBody AssignWorkScheduleRequest request) {
        WorkSchedule schedule = workScheduleService.assign(
                request.employeeId(), request.workDays(), request.startTime(), request.endTime());
        return ResponseEntity.ok(WorkScheduleResponse.from(schedule));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPERADMIN')")
    public ResponseEntity<WorkScheduleResponse> update(@PathVariable Long id,
                                                         @Valid @RequestBody UpdateWorkScheduleRequest request) {
        WorkSchedule schedule = workScheduleService.update(
                id, request.workDays(), request.startTime(), request.endTime());
        return ResponseEntity.ok(WorkScheduleResponse.from(schedule));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPERADMIN')")
    public ResponseEntity<Void> unassign(@PathVariable Long id) {
        workScheduleService.unassign(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPERADMIN')")
    public ResponseEntity<WorkScheduleResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(WorkScheduleResponse.from(workScheduleService.get(id)));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPERADMIN')")
    public ResponseEntity<WorkScheduleResponse> getByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(WorkScheduleResponse.from(workScheduleService.getByEmployee(employeeId)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPERADMIN')")
    public ResponseEntity<List<WorkScheduleResponse>> list() {
        List<WorkScheduleResponse> response = workScheduleService.listAll().stream()
                .map(WorkScheduleResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}

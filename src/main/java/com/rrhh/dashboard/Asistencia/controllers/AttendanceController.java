package com.rrhh.dashboard.Asistencia.controllers;

import com.rrhh.dashboard.Asistencia.Entity.AttendanceRecord;
import com.rrhh.dashboard.Asistencia.dtos.AttendanceRecordResponse;
import com.rrhh.dashboard.Asistencia.services.AttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/{employeeId}/clock-in")
    @PreAuthorize("hasAnyRole('EMPLEADO', 'ADMINISTRADOR', 'SUPERADMIN')")
    public ResponseEntity<AttendanceRecordResponse> clockIn(@PathVariable Long employeeId) {
        AttendanceRecord record = attendanceService.clockIn(employeeId, authenticatedEmployeeId());
        return ResponseEntity.ok(AttendanceRecordResponse.from(record));
    }

    @PostMapping("/{employeeId}/clock-out")
    @PreAuthorize("hasAnyRole('EMPLEADO', 'ADMINISTRADOR', 'SUPERADMIN')")
    public ResponseEntity<AttendanceRecordResponse> clockOut(@PathVariable Long employeeId) {
        AttendanceRecord record = attendanceService.clockOut(employeeId, authenticatedEmployeeId());
        return ResponseEntity.ok(AttendanceRecordResponse.from(record));
    }

    /**
     * Deriva el employeeId del empleado autenticado a partir de
     * Authentication.getName() (AuthenticatedEmployee.getName()), sin
     * depender del tipo concreto (vive en com.rrhh.dashboard.security).
     */
    private Long authenticatedEmployeeId() {
        return Long.valueOf(
                SecurityContextHolder.getContext().getAuthentication().getName());
    }
}

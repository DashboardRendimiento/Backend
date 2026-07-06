package com.rrhh.dashboard.Asistencia.dtos;

import com.rrhh.dashboard.Asistencia.Entity.AttendanceRecord;

import java.time.Instant;

public record AttendanceRecordResponse(
        Long id,
        Long employeeId,
        Instant clockInAt,
        Instant clockOutAt
) {
    public static AttendanceRecordResponse from(AttendanceRecord record) {
        return new AttendanceRecordResponse(
                record.getId(),
                record.getEmployeeId(),
                record.getClockInAt(),
                record.getClockOutAt());
    }
}

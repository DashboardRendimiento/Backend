package com.rrhh.dashboard.Asistencia.dtos;

import com.rrhh.dashboard.Asistencia.Entity.AttendanceRecord;
import com.rrhh.dashboard.Asistencia.Entity.EstadoVerificacionFacial;

import java.time.Instant;

/**
 * No incluye la foto capturada (bytes) para no inflar el JSON — se sirve
 * aparte via GET /api/attendance/{id}/foto.
 */
public record AttendanceRecordResponse(
        Long id,
        Long employeeId,
        Instant clockInAt,
        Instant clockOutAt,
        Double similitudFacial,
        EstadoVerificacionFacial estadoVerificacion
) {
    public static AttendanceRecordResponse from(AttendanceRecord record) {
        return new AttendanceRecordResponse(
                record.getId(),
                record.getEmployeeId(),
                record.getClockInAt(),
                record.getClockOutAt(),
                record.getSimilitudFacial(),
                record.getEstadoVerificacion());
    }
}

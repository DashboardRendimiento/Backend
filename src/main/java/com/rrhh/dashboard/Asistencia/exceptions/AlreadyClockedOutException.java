package com.rrhh.dashboard.Asistencia.exceptions;

/**
 * Invariante de la propia entidad AttendanceRecord: no se puede registrar
 * la "Salida" de un fichaje que ya la tiene.
 */
public class AlreadyClockedOutException extends RuntimeException {

    public AlreadyClockedOutException(Long attendanceRecordId) {
        super("El fichaje " + attendanceRecordId + " ya tiene una salida registrada");
    }
}

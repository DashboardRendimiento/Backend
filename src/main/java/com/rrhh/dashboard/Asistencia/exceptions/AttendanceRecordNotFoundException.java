package com.rrhh.dashboard.Asistencia.exceptions;

public class AttendanceRecordNotFoundException extends RuntimeException {

    public AttendanceRecordNotFoundException(Long id) {
        super("No existe un fichaje con id " + id);
    }
}

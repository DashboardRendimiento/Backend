package com.rrhh.dashboard.Asistencia.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * No existe un fichaje de "Entrada" abierto para el empleado indicado —
 * no se puede fichar "Salida" sin una entrada previa sin cerrar.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class NoOpenAttendanceRecordException extends RuntimeException {

    public NoOpenAttendanceRecordException(Long employeeId) {
        super("El empleado " + employeeId + " no tiene un fichaje de entrada abierto para registrar la salida");
    }
}

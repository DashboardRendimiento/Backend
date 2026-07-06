package com.rrhh.dashboard.Asistencia.exceptions;

/**
 * El empleado ya tiene un fichaje de "Entrada" abierto (sin "Salida"
 * registrada) — no puede fichar una segunda entrada hasta cerrar la
 * anterior.
 */
public class AlreadyClockedInException extends RuntimeException {

    public AlreadyClockedInException(Long employeeId) {
        super("El empleado " + employeeId + " ya tiene un fichaje de entrada sin salida registrada");
    }
}

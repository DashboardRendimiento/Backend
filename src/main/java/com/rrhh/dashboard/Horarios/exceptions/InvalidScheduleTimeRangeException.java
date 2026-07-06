package com.rrhh.dashboard.Horarios.exceptions;

/**
 * Invariante de WorkSchedule: la hora de salida esperada tiene que ser
 * posterior a la hora de entrada esperada. No modela turnos que cruzan la
 * medianoche.
 */
public class InvalidScheduleTimeRangeException extends RuntimeException {

    public InvalidScheduleTimeRangeException() {
        super("La hora de salida esperada debe ser posterior a la hora de entrada esperada");
    }
}

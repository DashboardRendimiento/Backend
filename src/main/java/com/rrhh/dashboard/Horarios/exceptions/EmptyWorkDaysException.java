package com.rrhh.dashboard.Horarios.exceptions;

/**
 * Invariante de WorkSchedule: un horario tiene que cubrir al menos un dia
 * de la semana.
 */
public class EmptyWorkDaysException extends RuntimeException {

    public EmptyWorkDaysException() {
        super("El horario debe incluir al menos un dia de la semana");
    }
}

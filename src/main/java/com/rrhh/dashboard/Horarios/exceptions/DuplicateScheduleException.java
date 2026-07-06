package com.rrhh.dashboard.Horarios.exceptions;

/**
 * El empleado indicado ya tiene un horario asignado (columna employeeId
 * unica en WorkSchedule). Para cambiarlo hay que usar la actualizacion
 * (WorkScheduleService.update), no crear uno nuevo.
 */
public class DuplicateScheduleException extends RuntimeException {

    public DuplicateScheduleException(Long employeeId) {
        super("El empleado " + employeeId + " ya tiene un horario asignado; use la actualizacion para modificarlo");
    }
}

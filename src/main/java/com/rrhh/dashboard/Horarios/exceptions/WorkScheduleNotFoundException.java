package com.rrhh.dashboard.Horarios.exceptions;

public class WorkScheduleNotFoundException extends RuntimeException {

    private WorkScheduleNotFoundException(String message) {
        super(message);
    }

    public static WorkScheduleNotFoundException byId(Long id) {
        return new WorkScheduleNotFoundException("No existe un horario con id " + id);
    }

    public static WorkScheduleNotFoundException forEmployee(Long employeeId) {
        return new WorkScheduleNotFoundException("El empleado " + employeeId + " no tiene un horario asignado");
    }
}

package com.rrhh.dashboard.registro_productividad.exceptions;

public class ForbiddenAttendanceAccessException extends RuntimeException {
    public ForbiddenAttendanceAccessException(Long employeeId, Long authenticatedEmployeeId) {
        super("El empleado autenticado (" + authenticatedEmployeeId
                + ") no puede fichar asistencia en nombre de otro empleado (" + employeeId + ")");
    }
}

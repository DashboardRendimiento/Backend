package com.rrhh.dashboard.Asistencia.exceptions;

/**
 * Se lanza cuando el empleado autenticado intenta fichar "Entrada"/"Salida"
 * en nombre de un employeeId que no es el propio: un Empleado solo puede
 * fichar su propia asistencia, no la de otros.
 */
public class ForbiddenAttendanceAccessException extends RuntimeException {

    public ForbiddenAttendanceAccessException(Long employeeId, Long authenticatedEmployeeId) {
        super("El empleado autenticado (" + authenticatedEmployeeId
                + ") no puede fichar asistencia en nombre de otro empleado (" + employeeId + ")");
    }
}

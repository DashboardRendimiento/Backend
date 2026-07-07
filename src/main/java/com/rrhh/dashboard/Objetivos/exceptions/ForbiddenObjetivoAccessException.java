package com.rrhh.dashboard.Objetivos.exceptions;

/**
 * Un Empleado solo puede ver sus propios objetivos — no los de otro
 * empleado (los roles de gestion si pueden ver cualquiera, se valida antes
 * en el controller).
 */
public class ForbiddenObjetivoAccessException extends RuntimeException {

    public ForbiddenObjetivoAccessException(Long empleadoId, Long authenticatedEmployeeId) {
        super("El empleado autenticado (" + authenticatedEmployeeId
                + ") no puede ver los objetivos de otro empleado (" + empleadoId + ")");
    }
}

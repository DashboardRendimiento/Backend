package com.rrhh.dashboard.Productividad.exceptions;

/**
 * El empleado ya cargo un registro de productividad en la hora calendario
 * actual — evita cargas duplicadas dentro de la misma hora.
 */
public class YaRegistradoEnEstaHoraException extends RuntimeException {

    public YaRegistradoEnEstaHoraException(Long empleadoId) {
        super("El empleado " + empleadoId + " ya registro su productividad en esta hora");
    }
}

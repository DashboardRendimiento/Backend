package com.rrhh.dashboard.Asistencia.Entity;

/**
 * Estado de la verificacion facial de un fichaje de Entrada (Salida no
 * lleva foto, queda siempre null). El fichaje se registra en todos los
 * casos — este estado nunca bloquea el clock-in, solo indica si hace
 * falta que un humano lo revise.
 */
public enum EstadoVerificacionFacial {
    VERIFICADO_AUTOMATICO,
    PENDIENTE_REVISION,
    VERIFICADO_MANUAL,
    RECHAZADO
}

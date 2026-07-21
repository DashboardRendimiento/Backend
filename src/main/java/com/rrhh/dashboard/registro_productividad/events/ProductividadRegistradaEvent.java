package com.rrhh.dashboard.registro_productividad.events;

/**
 * Se publica al guardar un registro de productividad. El listener del
 * broadcast por WebSocket recalcula el KPI recien despues del commit —
 * separado asi para que ProductividadService no dependa de como (ni si) se
 * notifica el cambio en vivo.
 */
public record ProductividadRegistradaEvent(Long empleadoId) {
}

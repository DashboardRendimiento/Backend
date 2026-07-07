package com.rrhh.dashboard.Objetivos.Entity;

/**
 * Extensible: hoy solo PEDIDOS tiene cruce automatico con lo cargado en
 * Productividad (ver ObjetivoService.calcularProgreso) — DINERO se guarda
 * igual, pero no hay ninguna fuente de datos de dinero en el sistema
 * todavia para calcular su progreso.
 */
public enum TipoObjetivo {
    PEDIDOS,
    DINERO
}

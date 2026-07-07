package com.rrhh.dashboard.Objetivos.dtos;

/**
 * cargadoHoy/cargadoSemana y sus pendientes quedan null para tipo DINERO:
 * no hay ninguna fuente de datos de dinero en el sistema todavia para
 * calcular el progreso automaticamente (ver ObjetivoService.calcularProgreso).
 */
public record ObjetivoProgresoResponse(
        ObjetivoResponse objetivo,
        Double cargadoHoy,
        Double pendienteHoy,
        Double cargadoSemana,
        Double pendienteSemana
) {
}

package com.rrhh.dashboard.Objetivos.dtos;

import com.rrhh.dashboard.Objetivos.Entity.Objetivo;
import com.rrhh.dashboard.Objetivos.Entity.TipoObjetivo;

import java.time.LocalDate;

public record ObjetivoResponse(
        Long id,
        Long empleadoId,
        TipoObjetivo tipo,
        Double valorSemanal,
        double valorDiario,
        LocalDate semanaInicio
) {
    public static ObjetivoResponse from(Objetivo objetivo) {
        return new ObjetivoResponse(
                objetivo.getId(),
                objetivo.getEmpleadoId(),
                objetivo.getTipo(),
                objetivo.getValorSemanal(),
                objetivo.getValorDiario(),
                objetivo.getSemanaInicio());
    }
}

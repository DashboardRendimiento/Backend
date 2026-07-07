package com.rrhh.dashboard.Objetivos.dtos;

import com.rrhh.dashboard.Objetivos.Entity.TipoObjetivo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record CrearObjetivoRequest(
        @NotNull Long empleadoId,
        @NotNull TipoObjetivo tipo,
        @NotNull @Positive Double valorSemanal,
        /** Lunes de la semana que aplica; si se omite, se toma el lunes de la semana actual. */
        LocalDate semanaInicio
) {
}

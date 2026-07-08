package com.rrhh.dashboard.Objetivos.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ActualizarObjetivoRequest(
        @NotNull @Positive Double valorSemanal
) {
}

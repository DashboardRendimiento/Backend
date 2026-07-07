package com.rrhh.dashboard.Productividad.Dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RegistroHorarioRequest(
        @NotNull @Min(0) Integer pedidosPreparados,
        @Min(0) Integer bultosPreparados
) {
}

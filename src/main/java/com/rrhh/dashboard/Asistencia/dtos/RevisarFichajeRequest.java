package com.rrhh.dashboard.Asistencia.dtos;

import jakarta.validation.constraints.NotNull;

public record RevisarFichajeRequest(
        @NotNull Boolean aprobado
) {
}

package com.rrhh.dashboard.registro_productividad.Dtos;

import java.time.LocalDateTime;

public record DashboardEventDTO(

        String type,

        Long empleadoId,

        LocalDateTime fecha

) {}
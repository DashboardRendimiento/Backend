package com.rrhh.dashboard.Productividad.Dtos;

import com.rrhh.dashboard.Productividad.Entity.RegistroHorario;

import java.time.Instant;

public record RegistroHorarioResponse(
        Long id,
        Long empleadoId,
        Integer pedidosPreparados,
        Integer bultosPreparados,
        Instant registradoEn
) {
    public static RegistroHorarioResponse from(RegistroHorario registro) {
        return new RegistroHorarioResponse(
                registro.getId(),
                registro.getEmpleadoId(),
                registro.getPedidosPreparados(),
                registro.getBultosPreparados(),
                registro.getRegistradoEn());
    }
}

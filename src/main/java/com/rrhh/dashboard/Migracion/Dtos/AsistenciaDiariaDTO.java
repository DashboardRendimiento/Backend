package com.rrhh.dashboard.Migracion.Dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AsistenciaDiariaDTO {

    private String idEmpleado;

    private LocalDate fecha;

    private String estado;

    private String horaEntrada;

    private String horaSalida;

    private Integer minutosTardanza;

}
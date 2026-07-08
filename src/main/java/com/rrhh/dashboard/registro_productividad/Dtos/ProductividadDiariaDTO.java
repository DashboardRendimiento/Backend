package com.rrhh.dashboard.registro_productividad.Dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProductividadDiariaDTO {

    private Long empleado;

    private LocalDate fecha;

    private Integer pedidosEncargados;

    private Integer pedidosPreparados;

    private Integer pedidosPendientes;

    private Integer bultosPreparados;

}


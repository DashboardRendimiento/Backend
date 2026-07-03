package com.rrhh.dashboard.Migracion.Dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProductividadDiariaDTO {

    private String idEmpleado;

    private LocalDate fecha;

    private Integer pedidosProcesados;

    private Integer pedidosEsperados;

    private Double productividad;

    private Integer errores;

    private Double eficiencia;

}
package com.rrhh.dashboard.Productividad.Dtos;

import java.time.LocalDate;

import com.rrhh.dashboard.Empleados.dtos.EmpleadoDTO;

import lombok.Data;

@Data
public class ProductividadResponseDto {
       private EmpleadoDTO empleado;

    private LocalDate fecha;

    private Integer pedidosEncargados;

    private Integer pedidosPreparados;

    private Integer pedidosPendientes;

    private Integer bultosPreparados;
}


 
package com.rrhh.dashboard.registro_productividad.Dtos;

import lombok.Data;

@Data
public class ProductividadEmpleadoKPI {
  private Long empleadoId;
    private String nombre;

    private Integer totalPedidos;
    private Double porcentajeCumplimiento;
    private Integer totalBultos;
    private Integer totalPendientes;
    private java.time.LocalDateTime ultimaHoraCarga;

}
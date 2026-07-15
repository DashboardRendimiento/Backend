package com.rrhh.dashboard.registro_productividad.Dtos;

import lombok.Data;

@Data
public class ProductividadKPIDTO {

    private Integer totalPedidos;

    private Integer totalBultos;

    private Integer totalPendientes;

    private Double horasTrabajadas;

    private Double pedidosPorHora;

    private Double bultosPorHora;

    private Double promedioPedidosPorJornada;

    private Double promedioBultosPorJornada;
    private Double objetivoPedidos;
    private Double pedidosPendientesObjetivo;
    private Double porcentajeCumplimiento;
    private java.time.LocalDateTime ultimaHoraCarga;
}
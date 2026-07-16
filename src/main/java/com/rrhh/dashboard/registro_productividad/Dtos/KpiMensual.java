package com.rrhh.dashboard.registro_productividad.Dtos;

import java.time.LocalDate;

import lombok.Data;
@Data
public class KpiMensual {
    private int pedidosMes;
    private LocalDate inicioMes;
    private LocalDate finMes;
}
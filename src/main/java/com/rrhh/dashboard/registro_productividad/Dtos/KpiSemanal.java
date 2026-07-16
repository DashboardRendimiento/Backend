package com.rrhh.dashboard.registro_productividad.Dtos;

import java.time.LocalDate;

import lombok.Data;
@Data
public class KpiSemanal {
    private int pedidosSemana;
    private double objetivoSemanal;
    private double porcentajeCumplimientoSemanal;
    private LocalDate inicioSemana;
    private LocalDate finSemana;
   
}

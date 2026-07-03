package com.rrhh.dashboard.Migracion.Dtos;
import lombok.Data;

@Data
public class ResumenKpiDTO {
      private String indicador;
    private Double valorActual;
    private Double meta;
    private Double porcentajeCumplimiento;
    private String estado;
}

package com.rrhh.dashboard.PlantillaHoras.Dtos;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PlantillaHorasDTO {
    private Long id;
    private Long empleadoId;
    private LocalDate fecha;
    private Double horasTrabajadas;
    private Double horasExtra;
    private Integer minutosTardanza;
}

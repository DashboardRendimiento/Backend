package com.rrhh.dashboard.registro_productividad.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromedioProductividadDTO {
    private double promedioPedidos;
    private double promedioBultos;
    private double totalJornadasOHoras; // Total de jornadas u horas consideradas
    
    // Constructor específico para cuando solo necesitamos el promedio
    public PromedioProductividadDTO(double promedioPedidos, double promedioBultos) {
        this.promedioPedidos = promedioPedidos;
        this.promedioBultos = promedioBultos;
        this.totalJornadasOHoras = 0;
    }
}

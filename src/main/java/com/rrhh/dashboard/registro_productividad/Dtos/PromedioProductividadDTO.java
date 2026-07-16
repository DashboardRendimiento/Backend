package com.rrhh.dashboard.registro_productividad.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class PromedioProductividadDTO {
    private double promedioPedidos;
    private double promedioBultos;  // Agregar este campo
    private long totalJornadasOHoras; // Total de jornadas u horas consideradas
    
    // Constructor para cuando solo tenemos promedio de pedidos
    public PromedioProductividadDTO(double promedioPedidos, long totalJornadasOHoras) {
        this.promedioPedidos = promedioPedidos;
        this.totalJornadasOHoras = totalJornadasOHoras;
    }
    
   
}
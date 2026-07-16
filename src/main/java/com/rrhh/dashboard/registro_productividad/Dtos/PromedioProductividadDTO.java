package com.rrhh.dashboard.registro_productividad.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class PromedioProductividadDTO {
    private double promedioPedidos;
    private double promedioBultos;
    private long totalJornadasOHoras; // Total de jornadas u horas consideradas
    
    // Constructor específico para cuando solo necesitamos el promedio
    public PromedioProductividadDTO(double promedioPedidos, double promedioBultos) {
        this.promedioPedidos = promedioPedidos;
        this.promedioBultos = promedioBultos;
        this.totalJornadasOHoras = 0;
    }

    public PromedioProductividadDTO() {}

    public PromedioProductividadDTO(double promedioPedidos, double promedioBultos, long totalJornadasOHoras) {
        this.promedioPedidos = promedioPedidos;
        this.promedioBultos = promedioBultos;
        this.totalJornadasOHoras = totalJornadasOHoras;
    }

    public double getPromedioPedidos() { return this.promedioPedidos; }
    public void setPromedioPedidos(double promedioPedidos) { this.promedioPedidos = promedioPedidos; }
    public double getPromedioBultos() { return this.promedioBultos; }
    public void setPromedioBultos(double promedioBultos) { this.promedioBultos = promedioBultos; }
    public long getTotalJornadasOHoras() { return this.totalJornadasOHoras; }
    public void setTotalJornadasOHoras(long totalJornadasOHoras) { this.totalJornadasOHoras = totalJornadasOHoras; }
}
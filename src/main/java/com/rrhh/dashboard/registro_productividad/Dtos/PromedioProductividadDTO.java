package com.rrhh.dashboard.registro_productividad.Dtos;


public class PromedioProductividadDTO {
    private double promedioPedidos;
    private double promedioBultos;  // Agregar este campo
    private long totalJornadasOHoras; // Total de jornadas u horas consideradas
    
    // Constructor para cuando solo tenemos promedio de pedidos
    public PromedioProductividadDTO(double promedioPedidos, long totalJornadasOHoras) {
        this.promedioPedidos = promedioPedidos;
        this.totalJornadasOHoras = totalJornadasOHoras;
    }
    
   
    public double getPromedioPedidos() { return this.promedioPedidos; }
    public void setPromedioPedidos(double promedioPedidos) { this.promedioPedidos = promedioPedidos; }
    public double getPromedioBultos() { return this.promedioBultos; }
    public void setPromedioBultos(double promedioBultos) { this.promedioBultos = promedioBultos; }
    public long getTotalJornadasOHoras() { return this.totalJornadasOHoras; }
    public void setTotalJornadasOHoras(long totalJornadasOHoras) { this.totalJornadasOHoras = totalJornadasOHoras; }
    public PromedioProductividadDTO() {}
    public PromedioProductividadDTO(double promedioPedidos, double promedioBultos, long totalJornadasOHoras) {
        this.promedioPedidos = promedioPedidos;
        this.promedioBultos = promedioBultos;
        this.totalJornadasOHoras = totalJornadasOHoras;
    }
}
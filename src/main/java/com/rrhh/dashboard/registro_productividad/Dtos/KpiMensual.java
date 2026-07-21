package com.rrhh.dashboard.registro_productividad.Dtos;

import java.time.LocalDate;

public class KpiMensual {
    private int pedidosMes;
    private LocalDate inicioMes;
    private LocalDate finMes;
    public int getPedidosMes() { return this.pedidosMes; }
    public void setPedidosMes(int pedidosMes) { this.pedidosMes = pedidosMes; }
    public LocalDate getInicioMes() { return this.inicioMes; }
    public void setInicioMes(LocalDate inicioMes) { this.inicioMes = inicioMes; }
    public LocalDate getFinMes() { return this.finMes; }
    public void setFinMes(LocalDate finMes) { this.finMes = finMes; }
}
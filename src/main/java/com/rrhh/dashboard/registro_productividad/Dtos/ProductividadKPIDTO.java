package com.rrhh.dashboard.registro_productividad.Dtos;

import java.time.LocalDate;

public class ProductividadKPIDTO {
    
    private int totalPedidos;
    private int pedidosDia;
    private double objetivoPedidos;
    private double pedidosPendientesObjetivo;
    private double porcentajeCumplimiento;
    private LocalDate fechaUtilizada;
    

    public int getTotalPedidos() { return this.totalPedidos; }
    public void setTotalPedidos(int totalPedidos) { this.totalPedidos = totalPedidos; }
    public int getPedidosDia() { return this.pedidosDia; }
    public void setPedidosDia(int pedidosDia) { this.pedidosDia = pedidosDia; }
    public double getObjetivoPedidos() { return this.objetivoPedidos; }
    public void setObjetivoPedidos(double objetivoPedidos) { this.objetivoPedidos = objetivoPedidos; }
    public double getPedidosPendientesObjetivo() { return this.pedidosPendientesObjetivo; }
    public void setPedidosPendientesObjetivo(double pedidosPendientesObjetivo) { this.pedidosPendientesObjetivo = pedidosPendientesObjetivo; }
    public double getPorcentajeCumplimiento() { return this.porcentajeCumplimiento; }
    public void setPorcentajeCumplimiento(double porcentajeCumplimiento) { this.porcentajeCumplimiento = porcentajeCumplimiento; }
    public LocalDate getFechaUtilizada() { return this.fechaUtilizada; }
    public void setFechaUtilizada(LocalDate fechaUtilizada) { this.fechaUtilizada = fechaUtilizada; }
}
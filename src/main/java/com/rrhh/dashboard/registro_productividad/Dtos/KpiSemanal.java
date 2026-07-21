package com.rrhh.dashboard.registro_productividad.Dtos;

import java.time.LocalDate;

public class KpiSemanal {
    private int pedidosSemana;
    private double objetivoSemanal;
    private double porcentajeCumplimientoSemanal;
    private LocalDate inicioSemana;
    private LocalDate finSemana;
   
    public int getPedidosSemana() { return this.pedidosSemana; }
    public void setPedidosSemana(int pedidosSemana) { this.pedidosSemana = pedidosSemana; }
    public double getObjetivoSemanal() { return this.objetivoSemanal; }
    public void setObjetivoSemanal(double objetivoSemanal) { this.objetivoSemanal = objetivoSemanal; }
    public double getPorcentajeCumplimientoSemanal() { return this.porcentajeCumplimientoSemanal; }
    public void setPorcentajeCumplimientoSemanal(double porcentajeCumplimientoSemanal) { this.porcentajeCumplimientoSemanal = porcentajeCumplimientoSemanal; }
    public LocalDate getInicioSemana() { return this.inicioSemana; }
    public void setInicioSemana(LocalDate inicioSemana) { this.inicioSemana = inicioSemana; }
    public LocalDate getFinSemana() { return this.finSemana; }
    public void setFinSemana(LocalDate finSemana) { this.finSemana = finSemana; }
}

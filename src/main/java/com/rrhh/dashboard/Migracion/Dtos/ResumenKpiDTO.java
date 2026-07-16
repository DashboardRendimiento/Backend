package com.rrhh.dashboard.Migracion.Dtos;

public class ResumenKpiDTO {
    private String indicador;
    private Double valorActual;
    private Double meta;
    private Double porcentajeCumplimiento;
    private String estado;

    public ResumenKpiDTO() {}

    public String getIndicador() { return indicador; }
    public void setIndicador(String indicador) { this.indicador = indicador; }

    public Double getValorActual() { return valorActual; }
    public void setValorActual(Double valorActual) { this.valorActual = valorActual; }

    public Double getMeta() { return meta; }
    public void setMeta(Double meta) { this.meta = meta; }

    public Double getPorcentajeCumplimiento() { return porcentajeCumplimiento; }
    public void setPorcentajeCumplimiento(Double porcentajeCumplimiento) { this.porcentajeCumplimiento = porcentajeCumplimiento; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}

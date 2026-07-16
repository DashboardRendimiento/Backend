package com.rrhh.dashboard.Migracion.Dtos;

import lombok.Data;

import java.time.LocalDate;

public class AsistenciaDiariaDTO {

    private String idEmpleado;

    private LocalDate fecha;

    private String estado;

    private String horaEntrada;

    private String horaSalida;

    private Integer minutosTardanza;


    public AsistenciaDiariaDTO() {}
    public String getIdEmpleado() { return this.idEmpleado; }
    public void setIdEmpleado(String idEmpleado) { this.idEmpleado = idEmpleado; }
    public LocalDate getFecha() { return this.fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getEstado() { return this.estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getHoraEntrada() { return this.horaEntrada; }
    public void setHoraEntrada(String horaEntrada) { this.horaEntrada = horaEntrada; }
    public String getHoraSalida() { return this.horaSalida; }
    public void setHoraSalida(String horaSalida) { this.horaSalida = horaSalida; }
    public Integer getMinutosTardanza() { return this.minutosTardanza; }
    public void setMinutosTardanza(Integer minutosTardanza) { this.minutosTardanza = minutosTardanza; }
}
package com.rrhh.dashboard.PlantillaHoras.Dtos;

import lombok.Data;
import java.time.LocalDate;

public class PlantillaHorasDTO {
    private Long id;
    private Long empleadoId;
    private LocalDate fecha;
    private Double horasTrabajadas;
    private Double horasExtra;
    private Integer minutosTardanza;
    private String turno;
    private Boolean licencia;
    private String horaEntrada;
    private String horaSalida;
    private String tipoLicencia;

    public PlantillaHorasDTO() {}
    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmpleadoId() { return this.empleadoId; }
    public void setEmpleadoId(Long empleadoId) { this.empleadoId = empleadoId; }
    public LocalDate getFecha() { return this.fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public Double getHorasTrabajadas() { return this.horasTrabajadas; }
    public void setHorasTrabajadas(Double horasTrabajadas) { this.horasTrabajadas = horasTrabajadas; }
    public Double getHorasExtra() { return this.horasExtra; }
    public void setHorasExtra(Double horasExtra) { this.horasExtra = horasExtra; }
    public Integer getMinutosTardanza() { return this.minutosTardanza; }
    public void setMinutosTardanza(Integer minutosTardanza) { this.minutosTardanza = minutosTardanza; }
    public String getTurno() { return this.turno; }
    public void setTurno(String turno) { this.turno = turno; }
    public Boolean getLicencia() { return this.licencia; }
    public void setLicencia(Boolean licencia) { this.licencia = licencia; }
    public String getHoraEntrada() { return this.horaEntrada; }
    public void setHoraEntrada(String horaEntrada) { this.horaEntrada = horaEntrada; }
    public String getHoraSalida() { return this.horaSalida; }
    public void setHoraSalida(String horaSalida) { this.horaSalida = horaSalida; }
    public String getTipoLicencia() { return this.tipoLicencia; }
    public void setTipoLicencia(String tipoLicencia) { this.tipoLicencia = tipoLicencia; }
}

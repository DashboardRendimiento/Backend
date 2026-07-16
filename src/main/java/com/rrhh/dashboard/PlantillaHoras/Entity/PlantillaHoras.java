package com.rrhh.dashboard.PlantillaHoras.Entity;

import com.rrhh.dashboard.Empleados.Entity.Empleados;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "plantilla_horas", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"empleado_id", "fecha"})
})
public class PlantillaHoras {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleados empleado;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "horas_trabajadas")
    private Double horasTrabajadas = 0.0;

    @Column(name = "horas_extra")
    private Double horasExtra = 0.0;

    @Column(name = "minutos_tardanza")
    private Integer minutosTardanza = 0;

    @Column(name = "turno")
    private String turno;

    @Column(name = "licencia")
    private Boolean licencia = false;

    @Column(name = "hora_entrada")
    private String horaEntrada;

    @Column(name = "hora_salida")
    private String horaSalida;

    @Column(name = "tipo_licencia")
    private String tipoLicencia;

    public PlantillaHoras() {}
    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }
    public Empleados getEmpleado() { return this.empleado; }
    public void setEmpleado(Empleados empleado) { this.empleado = empleado; }
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
    public void setLicencia(Boolean licencia) { this.licencia = licencia != null ? licencia : false; }
    
    public String getHoraEntrada() { return this.horaEntrada; }
    public void setHoraEntrada(String horaEntrada) { this.horaEntrada = horaEntrada; }
    public String getHoraSalida() { return this.horaSalida; }
    public void setHoraSalida(String horaSalida) { this.horaSalida = horaSalida; }
    public String getTipoLicencia() { return this.tipoLicencia; }
    public void setTipoLicencia(String tipoLicencia) { this.tipoLicencia = tipoLicencia; }
}

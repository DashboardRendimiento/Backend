package com.rrhh.dashboard.registro_productividad.Entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import com.rrhh.dashboard.Empleados.Entity.Empleados;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "asistencia_diaria")
public class AsistenciaDiaria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "fecha")
    private LocalDate fecha;
    
    @Column(name = "id_empleado")
    private String idEmpleado;
    
    @Column(name = "nombre")
    private String nombre;
    
    @Column(name = "estado")
    private String estado;
    
    @Column(name = "horas_trabajadas")
    private Double horasTrabajadas;
    
    @Column(name = "minutos_tardanza")
    private Integer minutosTardanza;
    
    @Column(name = "horas_extra")
    private Double horasExtra;
    
    @CreationTimestamp
    private LocalDateTime fechaCarga;
    
    @ManyToOne
    @JoinColumn(name = "empleado_id", referencedColumnName = "id")
    private Empleados empleado;

    public void save(AsistenciaDiaria asistencia) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    public AsistenciaDiaria() {}
    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getFecha() { return this.fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getIdEmpleado() { return this.idEmpleado; }
    public void setIdEmpleado(String idEmpleado) { this.idEmpleado = idEmpleado; }
    public String getNombre() { return this.nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEstado() { return this.estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Double getHorasTrabajadas() { return this.horasTrabajadas; }
    public void setHorasTrabajadas(Double horasTrabajadas) { this.horasTrabajadas = horasTrabajadas; }
    public Integer getMinutosTardanza() { return this.minutosTardanza; }
    public void setMinutosTardanza(Integer minutosTardanza) { this.minutosTardanza = minutosTardanza; }
    public Double getHorasExtra() { return this.horasExtra; }
    public void setHorasExtra(Double horasExtra) { this.horasExtra = horasExtra; }
    public LocalDateTime getFechaCarga() { return this.fechaCarga; }
    public void setFechaCarga(LocalDateTime fechaCarga) { this.fechaCarga = fechaCarga; }
    public Empleados getEmpleado() { return this.empleado; }
    public void setEmpleado(Empleados empleado) { this.empleado = empleado; }
}
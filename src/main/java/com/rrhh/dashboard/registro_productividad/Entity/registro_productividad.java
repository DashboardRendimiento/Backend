package com.rrhh.dashboard.registro_productividad.Entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.rrhh.dashboard.Asistencia.Entity.AttendanceRecord;
import com.rrhh.dashboard.Empleados.Entity.Empleados;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "registro_productividad")
public class registro_productividad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "fecha")
    private LocalDate fecha;
        
    @Column(name = "bultos_preparados")
    private Integer bultosPreparados;
    
    @Column(name = "pedidos_preparados")
    private Integer pedidosPreparados;

    @Column(name = "pedidos_encargados")
    private Integer pedidosEncargados;

    
    @CreationTimestamp
    private LocalDateTime fechaCarga;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id", nullable = false)
    @JsonIgnore
    private AttendanceRecord asistencia;
        
    @ManyToOne
    @JoinColumn(name = "empleado_id" )  // Edl referencedColumnName no es necesario si la PK se llama "id"
    private Empleados empleado;


    public registro_productividad() {}
    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getFecha() { return this.fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public Integer getBultosPreparados() { return this.bultosPreparados; }
    public void setBultosPreparados(Integer bultosPreparados) { this.bultosPreparados = bultosPreparados; }
    public Integer getPedidosPreparados() { return this.pedidosPreparados; }
    public void setPedidosPreparados(Integer pedidosPreparados) { this.pedidosPreparados = pedidosPreparados; }
    public Integer getPedidosEncargados() { return this.pedidosEncargados; }
    public void setPedidosEncargados(Integer pedidosEncargados) { this.pedidosEncargados = pedidosEncargados; }
    public LocalDateTime getFechaCarga() { return this.fechaCarga; }
    public void setFechaCarga(LocalDateTime fechaCarga) { this.fechaCarga = fechaCarga; }
    public AttendanceRecord getAsistencia() { return this.asistencia; }
    public void setAsistencia(AttendanceRecord asistencia) { this.asistencia = asistencia; }
    public Empleados getEmpleado() { return this.empleado; }
    public void setEmpleado(Empleados empleado) { this.empleado = empleado; }
}

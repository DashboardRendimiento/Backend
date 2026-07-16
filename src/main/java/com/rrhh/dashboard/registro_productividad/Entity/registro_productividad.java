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
@Data
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


    @Column(name="fechaHora")
    private LocalDateTime fechaHora;
    
    @CreationTimestamp
    private LocalDateTime fechaCarga;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id", nullable = false)
    @JsonIgnore
    private AttendanceRecord asistencia;
        
    @ManyToOne
    @JoinColumn(name = "empleado_id" )  // Edl referencedColumnName no es necesario si la PK se llama "id"
    private Empleados empleado;

}


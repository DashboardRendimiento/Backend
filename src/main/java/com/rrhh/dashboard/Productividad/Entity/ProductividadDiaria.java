package com.rrhh.dashboard.Productividad.Entity;

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
import lombok.Data;

@Entity
@Table(name = "productividad_diaria")
@Data
public class ProductividadDiaria {
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
    
    @Column(name = "pedidos_pendientes")
    private Integer pedidosPendientes;
    
    @CreationTimestamp
    private LocalDateTime fechaCarga;
    
    @ManyToOne
    @JoinColumn(name = "empleado_id" )  // Edl referencedColumnName no es necesario si la PK se llama "id"
    private Empleados empleado;

}
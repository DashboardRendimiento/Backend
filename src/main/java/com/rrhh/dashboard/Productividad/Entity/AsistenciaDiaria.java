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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "asistencia_diaria")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
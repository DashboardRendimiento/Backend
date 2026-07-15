package com.rrhh.dashboard.PlantillaHoras.Entity;

import com.rrhh.dashboard.Empleados.Entity.Empleados;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "plantilla_horas", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"empleado_id", "fecha"})
})
@Data
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
}

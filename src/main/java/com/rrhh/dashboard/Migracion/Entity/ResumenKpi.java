package com.rrhh.dashboard.Migracion.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "ResumenKpi")
@Data
public class ResumenKpi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "indicador", nullable = false)
    private String indicador;

    @Column(name = "valor_actual")
    private Double valorActual;

    @Column(name = "meta")
    private Double meta;

    @Column(name = "porcentaje_cumplimiento")
    private Double porcentajeCumplimiento;

    @Column(name = "estado")
    private String estado;
}

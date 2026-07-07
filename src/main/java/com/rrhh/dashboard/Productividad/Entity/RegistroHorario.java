package com.rrhh.dashboard.Productividad.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Carga horaria de productividad por autorreporte del propio Empleado:
 * cuantos pedidos (y bultos) preparo en la ultima hora. El momento del
 * registro lo pone el servidor (Instant.now()), nunca el cliente — mismo
 * criterio que Asistencia.Entity.AttendanceRecord.
 */
@Entity
@Table(name = "registros_horarios")
public class RegistroHorario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long empleadoId;

    @Column(nullable = false)
    private Integer pedidosPreparados;

    private Integer bultosPreparados;

    @Column(nullable = false, updatable = false)
    private Instant registradoEn;

    protected RegistroHorario() {
        // JPA
    }

    public RegistroHorario(Long empleadoId, Integer pedidosPreparados, Integer bultosPreparados, Instant registradoEn) {
        this.empleadoId = empleadoId;
        this.pedidosPreparados = pedidosPreparados;
        this.bultosPreparados = bultosPreparados;
        this.registradoEn = registradoEn;
    }

    public Long getId() {
        return id;
    }

    public Long getEmpleadoId() {
        return empleadoId;
    }

    public Integer getPedidosPreparados() {
        return pedidosPreparados;
    }

    public Integer getBultosPreparados() {
        return bultosPreparados;
    }

    public Instant getRegistradoEn() {
        return registradoEn;
    }
}

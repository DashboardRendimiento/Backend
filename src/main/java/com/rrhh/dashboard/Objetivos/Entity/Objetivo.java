package com.rrhh.dashboard.Objetivos.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Objetivo semanal asignado por el Administrador/SuperAdmin a un empleado
 * (ej. cantidad de pedidos, o de dinero, a cumplir en la semana).
 *
 * <p>{@link #getValorDiario()} divide el valor semanal por 6 dias
 * laborales fijos — division simple pedida explicitamente, no derivada de
 * WorkSchedule (que podria tener otra cantidad de dias trabajados por
 * empleado).</p>
 */
@Entity
@Table(name = "objetivos")
public class Objetivo {

    private static final int DIAS_LABORALES = 6;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long empleadoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoObjetivo tipo;

    @Column(nullable = false)
    private Double valorSemanal;

    @Column(nullable = false)
    private LocalDate semanaInicio;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected Objetivo() {
        // JPA
    }

    public Objetivo(Long empleadoId, TipoObjetivo tipo, Double valorSemanal, LocalDate semanaInicio) {
        this.empleadoId = empleadoId;
        this.tipo = tipo;
        this.semanaInicio = semanaInicio;
        setValorSemanal(valorSemanal);
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public void setValorSemanal(Double valorSemanal) {
        if (valorSemanal == null || valorSemanal <= 0) {
            throw new com.rrhh.dashboard.Objetivos.exceptions.InvalidObjetivoException();
        }
        this.valorSemanal = valorSemanal;
    }

    public double getValorDiario() {
        return valorSemanal / DIAS_LABORALES;
    }

    public Long getId() {
        return id;
    }

    public Long getEmpleadoId() {
        return empleadoId;
    }

    public TipoObjetivo getTipo() {
        return tipo;
    }

    public Double getValorSemanal() {
        return valorSemanal;
    }

    public LocalDate getSemanaInicio() {
        return semanaInicio;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}

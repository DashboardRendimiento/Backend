package com.rrhh.dashboard.Horarios.Entity;

import com.rrhh.dashboard.Horarios.exceptions.EmptyWorkDaysException;
import com.rrhh.dashboard.Horarios.exceptions.InvalidScheduleTimeRangeException;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.Set;

/**
 * Horario de trabajo asignado por el Administrador a un empleado: dias de
 * la semana trabajados + hora de entrada/salida esperada. Un empleado
 * tiene, a lo sumo, un WorkSchedule vigente a la vez (columna employeeId
 * unica) — asignar uno nuevo reemplaza al anterior via
 * WorkScheduleService.update, no se acumula historial.
 */
@Entity
@Table(name = "work_schedules")
public class WorkSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long employeeId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "work_schedule_days", joinColumns = @JoinColumn(name = "work_schedule_id"))
    @Column(name = "day_of_week", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<DayOfWeek> workDays;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected WorkSchedule() {
        // JPA
    }

    public WorkSchedule(Long employeeId, Set<DayOfWeek> workDays, LocalTime startTime, LocalTime endTime) {
        this.employeeId = employeeId;
        applyChange(workDays, startTime, endTime);
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

    /**
     * Reemplaza dias/horario de este WorkSchedule, validando el mismo
     * invariante minimo que el constructor.
     */
    public void applyChange(Set<DayOfWeek> workDays, LocalTime startTime, LocalTime endTime) {
        if (workDays == null || workDays.isEmpty()) {
            throw new EmptyWorkDaysException();
        }
        if (startTime == null || endTime == null || !endTime.isAfter(startTime)) {
            throw new InvalidScheduleTimeRangeException();
        }
        this.workDays = EnumSet.copyOf(workDays);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getId() {
        return id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public Set<DayOfWeek> getWorkDays() {
        return workDays;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}

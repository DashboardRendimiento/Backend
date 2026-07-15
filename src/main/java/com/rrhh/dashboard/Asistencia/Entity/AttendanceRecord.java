package com.rrhh.dashboard.Asistencia.Entity;

import com.rrhh.dashboard.Asistencia.exceptions.AlreadyClockedOutException;
import com.rrhh.dashboard.Asistencia.exceptions.RevisionNoAplicableException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Fichaje de asistencia de un empleado, por autorreporte: botones
 * "Entrada"/"Salida", cada uno con fecha/hora capturada automaticamente en
 * el servidor (nunca un valor provisto por el cliente).
 */
@Entity
@Table(name = "attendance_records")
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long employeeId;

    @Column(nullable = false, updatable = false)
    private Instant clockInAt;

    private Instant clockOutAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @Lob
    private byte[] fotoCapturada;

    private Double similitudFacial;

    @Enumerated(EnumType.STRING)
    private EstadoVerificacionFacial estadoVerificacion;

    protected AttendanceRecord() {
        // JPA
    }

    public AttendanceRecord(Long employeeId) {
        this.employeeId = employeeId;
        this.clockInAt = Instant.now();
    }

    /**
     * Completa el resultado de la verificacion facial de este fichaje de
     * Entrada — nunca lanza ni bloquea nada, el fichaje ya esta registrado
     * antes de llamar esto (ver AttendanceService.clockIn).
     */
    public void registrarVerificacionFacial(byte[] fotoCapturada, Double similitudFacial,
                                             EstadoVerificacionFacial estadoVerificacion) {
        this.fotoCapturada = fotoCapturada;
        this.similitudFacial = similitudFacial;
        this.estadoVerificacion = estadoVerificacion;
    }

    /**
     * Resuelve una verificacion pendiente: un humano (Administrador/
     * SuperAdmin/Supervisor) vio la foto capturada y decide si corresponde
     * al empleado o no. No se puede revisar un fichaje que no esta
     * pendiente (ya resuelto automaticamente, o ya revisado antes).
     */
    public void revisarManualmente(boolean aprobado) {
        this.estadoVerificacion = aprobado
                ? EstadoVerificacionFacial.VERIFICADO_MANUAL
                : EstadoVerificacionFacial.RECHAZADO;
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
     * Invariante de la propia entidad: no se puede fichar salida dos veces
     * sobre el mismo registro de entrada.
     */
    public void registerClockOut() {
        if (this.clockOutAt != null) {
            throw new AlreadyClockedOutException(this.id);
        }
        this.clockOutAt = Instant.now();
    }

    public boolean isOpen() {
        return this.clockOutAt == null;
    }

    public Long getId() {
        return id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public Instant getClockInAt() {
        return clockInAt;
    }

    public Instant getClockOutAt() {
        return clockOutAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public byte[] getFotoCapturada() {
        return fotoCapturada;
    }

    public Double getSimilitudFacial() {
        return similitudFacial;
    }

    public EstadoVerificacionFacial getEstadoVerificacion() {
        return estadoVerificacion;
    }

    public static Object builder() {
        throw new UnsupportedOperationException("Unimplemented method 'builder'");
    }
}

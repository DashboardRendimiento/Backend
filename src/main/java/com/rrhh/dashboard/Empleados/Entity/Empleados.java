package com.rrhh.dashboard.Empleados.Entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "empleados")
public class Empleados {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="nombre")
    private String nombre;
    @Column (name ="apellido")
    private String apellido;
    @Column (name = "dni")
    private Long dni;
    @Column ( name="sector")
    private String sector;
    @Column (name="puesto")
    private String puesto;
    @Column (name="turno")
    private String turno;
    @Column(name = "email", unique = true)
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password_hash")
    private String passwordHash;
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private EmployeeRole role = EmployeeRole.EMPLEADO;
    @Column(name = "active")
    private boolean active = true;
    @JsonIgnore
    @Lob
    @Column(name = "foto_referencia")
    private byte[] fotoReferencia;
    public void setIdEmpleado(int idEmpleado) {
    this.id = (long) idEmpleado;
    }
    public void setTurno(String turno){
        this.turno = turno;
    }


    public Empleados() {}
    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return this.nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return this.apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public Long getDni() { return this.dni; }
    public void setDni(Long dni) { this.dni = dni; }
    public String getSector() { return this.sector; }
    public void setSector(String sector) { this.sector = sector; }
    public String getPuesto() { return this.puesto; }
    public void setPuesto(String puesto) { this.puesto = puesto; }
    public String getTurno() { return this.turno; }
    public String getEmail() { return this.email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return this.passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public EmployeeRole getRole() { return this.role; }
    public void setRole(EmployeeRole role) { this.role = role; }
    public boolean getActive() { return this.active; }
    public void setActive(boolean active) { this.active = active; }
    public byte[] getFotoReferencia() { return this.fotoReferencia; }
    public void setFotoReferencia(byte[] fotoReferencia) { this.fotoReferencia = fotoReferencia; }
}
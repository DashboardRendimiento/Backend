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
@Data
@NoArgsConstructor
@AllArgsConstructor
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

}
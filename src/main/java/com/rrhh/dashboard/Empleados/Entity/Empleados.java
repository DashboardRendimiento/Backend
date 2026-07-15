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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "El nombre no puede estar vacÃ­o")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @NotBlank(message = "El apellido no puede estar vacÃ­o")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    @Column(name = "apellido", nullable = false)
    private String apellido;

    @NotNull(message = "El DNI no puede ser nulo")
    @Column(name = "dni", unique = true, nullable = false)
    private Long dni;

    @NotBlank(message = "El sector no puede estar vacÃ­o")
    @Column(name = "sector", nullable = false)
    private String sector;

    @NotBlank(message = "El puesto no puede estar vacÃ­o")
    @Column(name = "puesto", nullable = false)
    private String puesto;

    @NotBlank(message = "El turno no puede estar vacÃ­o")
    @Column(name = "turno", nullable = false)
    private String turno;

    @NotBlank(message = "El email no puede estar vacÃ­o")
    @Email(message = "El email debe tener un formato vÃ¡lido")
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @NotBlank(message = "La contraseÃ±a no puede estar vacÃ­a")
    @Size(min = 6, message = "La contraseÃ±a debe tener al menos 6 caracteres")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private EmployeeRole role = EmployeeRole.EMPLEADO;

    @Column(name = "active", nullable = false)
    private boolean active = true;


    // MÃ©todos existentes

    @JsonIgnore
    @Lob
    @Column(name = "foto_referencia")
    private byte[] fotoReferencia;

    public void setIdEmpleado(int idEmpleado) {
        this.id = (long) idEmpleado;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }
}

package com.rrhh.dashboard.Empleados.Entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private Number dni;
    @Column ( name="sector")
    private String sector;
    @Column (name="puesto")
    private String puesto;
    @Column (name="turno")
    private String turno;
    public void setIdEmpleado(int idEmpleado) {
    this.id = (long) idEmpleado;
    }
    public void setTurno(String turno){
        this.turno = turno;
    }

}
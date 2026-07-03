package com.rrhh.dashboard.Empleados.dtos;

import lombok.Data;

@Data
public class EmpleadoDTO {

    private String idEmpleado;
    private String nombre;
    private String apellido;
    private Number dni;
    private String sector;
    private String puesto;
    private String turno;
}
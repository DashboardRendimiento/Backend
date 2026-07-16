package com.rrhh.dashboard.Empleados.dtos;

import lombok.Data;

public class EmpleadoDTO {

    private String idEmpleado;
    private String nombre;
    private String apellido;
    private long dni;
    private String sector;
    private String puesto;
    private String turno;
    private String password;  

    public String getIdEmpleado() { return this.idEmpleado; }
    public void setIdEmpleado(String idEmpleado) { this.idEmpleado = idEmpleado; }
    public String getNombre() { return this.nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return this.apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public long getDni() { return this.dni; }
    public void setDni(long dni) { this.dni = dni; }
    public String getSector() { return this.sector; }
    public void setSector(String sector) { this.sector = sector; }
    public String getPuesto() { return this.puesto; }
    public void setPuesto(String puesto) { this.puesto = puesto; }
    public String getTurno() { return this.turno; }
    public void setTurno(String turno) { this.turno = turno; }
    public String getPassword() { return this.password; }
    public void setPassword(String password) { this.password = password; }
}
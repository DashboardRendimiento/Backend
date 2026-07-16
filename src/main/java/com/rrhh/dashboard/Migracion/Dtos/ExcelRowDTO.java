package com.rrhh.dashboard.Migracion.Dtos;


import lombok.Data;
import java.time.LocalDate;

public class ExcelRowDTO {
    // Datos de empleado
    private String nombre;
    private String apellido;
    private Long dni;  // Cambiar a Long
    private String sector;
    private String puesto;
    private String turno;
    
    // Datos de productividad diaria
    private LocalDate fecha;
    private Integer bultosPreparados;
    private Integer pedidosPreparados;
    private Integer pedidosEncargados;
    private Integer pedidosPendientes;
    
    // Datos de asistencia diaria
    private String idEmpleado;
    private String estado;
    private Double horasTrabajadas;
    private Integer minutosTardanza;
    private Double horasExtra;

    public ExcelRowDTO() {}
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
    public void setTurno(String turno) { this.turno = turno; }
    public LocalDate getFecha() { return this.fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public Integer getBultosPreparados() { return this.bultosPreparados; }
    public void setBultosPreparados(Integer bultosPreparados) { this.bultosPreparados = bultosPreparados; }
    public Integer getPedidosPreparados() { return this.pedidosPreparados; }
    public void setPedidosPreparados(Integer pedidosPreparados) { this.pedidosPreparados = pedidosPreparados; }
    public Integer getPedidosEncargados() { return this.pedidosEncargados; }
    public void setPedidosEncargados(Integer pedidosEncargados) { this.pedidosEncargados = pedidosEncargados; }
    public Integer getPedidosPendientes() { return this.pedidosPendientes; }
    public void setPedidosPendientes(Integer pedidosPendientes) { this.pedidosPendientes = pedidosPendientes; }
    public String getIdEmpleado() { return this.idEmpleado; }
    public void setIdEmpleado(String idEmpleado) { this.idEmpleado = idEmpleado; }
    public String getEstado() { return this.estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Double getHorasTrabajadas() { return this.horasTrabajadas; }
    public void setHorasTrabajadas(Double horasTrabajadas) { this.horasTrabajadas = horasTrabajadas; }
    public Integer getMinutosTardanza() { return this.minutosTardanza; }
    public void setMinutosTardanza(Integer minutosTardanza) { this.minutosTardanza = minutosTardanza; }
    public Double getHorasExtra() { return this.horasExtra; }
    public void setHorasExtra(Double horasExtra) { this.horasExtra = horasExtra; }
}
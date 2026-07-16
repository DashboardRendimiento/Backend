package com.rrhh.dashboard.registro_productividad.Dtos;

import lombok.Data;

import java.time.LocalDate;

public class ProductividadDiariaDTO {

    private Long empleado;

    private LocalDate fecha;

    private Integer pedidosEncargados;

    private Integer pedidosPreparados;

    private Integer pedidosPendientes;

    private Integer bultosPreparados;


    public ProductividadDiariaDTO() {}
    public Long getEmpleado() { return this.empleado; }
    public void setEmpleado(Long empleado) { this.empleado = empleado; }
    public LocalDate getFecha() { return this.fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public Integer getPedidosEncargados() { return this.pedidosEncargados; }
    public void setPedidosEncargados(Integer pedidosEncargados) { this.pedidosEncargados = pedidosEncargados; }
    public Integer getPedidosPreparados() { return this.pedidosPreparados; }
    public void setPedidosPreparados(Integer pedidosPreparados) { this.pedidosPreparados = pedidosPreparados; }
    public Integer getPedidosPendientes() { return this.pedidosPendientes; }
    public void setPedidosPendientes(Integer pedidosPendientes) { this.pedidosPendientes = pedidosPendientes; }
    public Integer getBultosPreparados() { return this.bultosPreparados; }
    public void setBultosPreparados(Integer bultosPreparados) { this.bultosPreparados = bultosPreparados; }
}


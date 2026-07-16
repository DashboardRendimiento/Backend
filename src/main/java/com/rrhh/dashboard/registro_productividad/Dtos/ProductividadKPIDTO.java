package com.rrhh.dashboard.registro_productividad.Dtos;

public class ProductividadKPIDTO {
    private Integer totalPedidos;
    private Integer totalBultos;
    private Integer totalPendientes;
    private Double horasTrabajadas;
    private Double pedidosPorHora;
    private Double bultosPorHora;
    private Double promedioPedidosPorJornada;
    private Double promedioBultosPorJornada;
    private Double objetivoPedidos;
    private Double pedidosPendientesObjetivo;
    private Double porcentajeCumplimiento;
    private java.time.LocalDateTime ultimaHoraCarga;

    public ProductividadKPIDTO() {}

    public Integer getTotalPedidos() { return totalPedidos; }
    public void setTotalPedidos(Integer totalPedidos) { this.totalPedidos = totalPedidos; }
    public void setTotalPedidos(int totalPedidos) { this.totalPedidos = totalPedidos; }

    public Integer getTotalBultos() { return totalBultos; }
    public void setTotalBultos(Integer totalBultos) { this.totalBultos = totalBultos; }
    public void setTotalBultos(int totalBultos) { this.totalBultos = totalBultos; }

    public Integer getTotalPendientes() { return totalPendientes; }
    public void setTotalPendientes(Integer totalPendientes) { this.totalPendientes = totalPendientes; }

    public Double getHorasTrabajadas() { return horasTrabajadas; }
    public void setHorasTrabajadas(Double horasTrabajadas) { this.horasTrabajadas = horasTrabajadas; }

    public Double getPedidosPorHora() { return pedidosPorHora; }
    public void setPedidosPorHora(Double pedidosPorHora) { this.pedidosPorHora = pedidosPorHora; }
    public void setPedidosPorHora(double pedidosPorHora) { this.pedidosPorHora = pedidosPorHora; }

    public Double getBultosPorHora() { return bultosPorHora; }
    public void setBultosPorHora(Double bultosPorHora) { this.bultosPorHora = bultosPorHora; }
    public void setBultosPorHora(double bultosPorHora) { this.bultosPorHora = bultosPorHora; }

    public Double getPromedioPedidosPorJornada() { return promedioPedidosPorJornada; }
    public void setPromedioPedidosPorJornada(Double promedioPedidosPorJornada) { this.promedioPedidosPorJornada = promedioPedidosPorJornada; }
    public void setPromedioPedidosPorJornada(double promedioPedidosPorJornada) { this.promedioPedidosPorJornada = promedioPedidosPorJornada; }

    public Double getPromedioBultosPorJornada() { return promedioBultosPorJornada; }
    public void setPromedioBultosPorJornada(Double promedioBultosPorJornada) { this.promedioBultosPorJornada = promedioBultosPorJornada; }
    public void setPromedioBultosPorJornada(double promedioBultosPorJornada) { this.promedioBultosPorJornada = promedioBultosPorJornada; }

    public Double getObjetivoPedidos() { return objetivoPedidos; }
    public void setObjetivoPedidos(Double objetivoPedidos) { this.objetivoPedidos = objetivoPedidos; }
    public void setObjetivoPedidos(double objetivoPedidos) { this.objetivoPedidos = objetivoPedidos; }

    public Double getPedidosPendientesObjetivo() { return pedidosPendientesObjetivo; }
    public void setPedidosPendientesObjetivo(Double pedidosPendientesObjetivo) { this.pedidosPendientesObjetivo = pedidosPendientesObjetivo; }
    public void setPedidosPendientesObjetivo(double pedidosPendientesObjetivo) { this.pedidosPendientesObjetivo = pedidosPendientesObjetivo; }

    public Double getPorcentajeCumplimiento() { return porcentajeCumplimiento; }
    public void setPorcentajeCumplimiento(Double porcentajeCumplimiento) { this.porcentajeCumplimiento = porcentajeCumplimiento; }
    public void setPorcentajeCumplimiento(double porcentajeCumplimiento) { this.porcentajeCumplimiento = porcentajeCumplimiento; }

    public java.time.LocalDateTime getUltimaHoraCarga() { return ultimaHoraCarga; }
    public void setUltimaHoraCarga(java.time.LocalDateTime ultimaHoraCarga) { this.ultimaHoraCarga = ultimaHoraCarga; }
    public <T> void setUltimaHoraCarga(T ultimaHoraCarga) { 
        if (ultimaHoraCarga instanceof java.time.LocalDateTime) {
            this.ultimaHoraCarga = (java.time.LocalDateTime) ultimaHoraCarga; 
        }
    }
}
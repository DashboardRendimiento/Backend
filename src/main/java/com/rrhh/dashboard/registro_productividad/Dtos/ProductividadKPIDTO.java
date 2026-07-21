package com.rrhh.dashboard.registro_productividad.Dtos;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ProductividadKPIDTO {
    
    private int totalPedidos;
    private int pedidosDia;
    private double objetivoPedidos;
    private double pedidosPendientesObjetivo;
    private double porcentajeCumplimiento;
    private LocalDate fechaUtilizada;
    

}
package com.rrhh.dashboard.Migracion.Dtos;


import lombok.Data;
import java.time.LocalDate;

@Data
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
    private Integer pedidosPendientes;
    
    // Datos de asistencia diaria
    private String idEmpleado;
    private String estado;
    private Double horasTrabajadas;
    private Integer minutosTardanza;
    private Double horasExtra;
}
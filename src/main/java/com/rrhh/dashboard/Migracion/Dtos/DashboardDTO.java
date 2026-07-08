package com.rrhh.dashboard.Migracion.Dtos;

import lombok.Data;

import java.util.List;

import com.rrhh.dashboard.Empleados.dtos.EmpleadoDTO;
import com.rrhh.dashboard.registro_productividad.Dtos.ProductividadDiariaDTO;

@Data
public class DashboardDTO {

    private List<ResumenKpiDTO> resumen;

    private List<EmpleadoDTO> empleados;

    private List<ProductividadDiariaDTO> productividad;

    private List<AsistenciaDiariaDTO> asistencia;


}
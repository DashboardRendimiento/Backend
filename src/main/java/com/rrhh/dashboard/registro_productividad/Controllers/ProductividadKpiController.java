package com.rrhh.dashboard.registro_productividad.Controllers;

import com.rrhh.dashboard.registro_productividad.Dtos.*;
import com.rrhh.dashboard.registro_productividad.Service.ProductividadKPIService;


import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/productividad")
public class ProductividadKpiController {

    private final ProductividadKPIService service;
    public ProductividadKpiController(ProductividadKPIService service) {
        this.service = service;
    }



    // ==================================================
    // NUEVOS ENDPOINTS PARA KPI
    // ==================================================

    @GetMapping("/kpi/mi-kpi")
    @PreAuthorize("hasAnyRole('EMPLEADO', 'SUPERVISOR', 'ADMINISTRADOR', 'SUPERADMIN')")
    public ResponseEntity<ProductividadKPIDTO> getMiKPI() {
        return ResponseEntity.ok(service.obtenerMiKPI());
    }

    @GetMapping({"/kpi/empleado/{empleadoId}", "/kpi/{empleadoId}"})
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPERVISOR', 'SUPERADMIN')")
    public ResponseEntity<ProductividadKPIDTO> getKPIEmpleado(@PathVariable Long empleadoId) {
        return ResponseEntity.ok(service.obtenerKPI(empleadoId));
    }

    @GetMapping("/kpi/semanal/{empleadoId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPERVISOR', 'SUPERADMIN')")
    public ResponseEntity<KpiSemanal> getKPISemanal(@PathVariable Long empleadoId) {
        return ResponseEntity.ok(service.obtenerKPISoloSemanal(empleadoId));
    }

    @GetMapping("/kpi/mensual/{empleadoId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPERVISOR', 'SUPERADMIN')")
    public ResponseEntity<KpiMensual> getKPIMensual(@PathVariable Long empleadoId) {
        return ResponseEntity.ok(service.obtenerKPISoloMensual(empleadoId));
    }

    @GetMapping("/kpi/mi-kpi-semanal")
    @PreAuthorize("hasAnyRole('EMPLEADO', 'SUPERVISOR', 'ADMINISTRADOR', 'SUPERADMIN')")
    public ResponseEntity<KpiSemanal> getMiKPISemanal() {
        return ResponseEntity.ok(service.obtenerKPISoloSemanal(
                service.obtenerEmpleadoAutenticado().getId()
        ));
    }

    @GetMapping("/kpi/mi-kpi-mensual")
    @PreAuthorize("hasAnyRole('EMPLEADO', 'SUPERVISOR', 'ADMINISTRADOR', 'SUPERADMIN')")
    public ResponseEntity<KpiMensual> getMiKPIMensual() {
        return ResponseEntity.ok(service.obtenerKPISoloMensual(
                service.obtenerEmpleadoAutenticado().getId()
        ));
    }


}
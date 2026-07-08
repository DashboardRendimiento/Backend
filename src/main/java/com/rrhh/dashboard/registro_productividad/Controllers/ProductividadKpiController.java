package com.rrhh.dashboard.registro_productividad.Controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.rrhh.dashboard.registro_productividad.Dtos.ProductividadKPIDTO;
import com.rrhh.dashboard.registro_productividad.Service.ProductividadKPIService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/productividad/kpi")
@RequiredArgsConstructor
public class ProductividadKpiController {

    private final ProductividadKPIService productividadKPIService;

    /**
     * KPI del usuario autenticado
     */
    @GetMapping("/me")
        @PreAuthorize("hasRole('EMPLEADO')")

    public ResponseEntity<ProductividadKPIDTO> obtenerMiKPI() {
        return ResponseEntity.ok(productividadKPIService.obtenerMiKPI());
    }

}

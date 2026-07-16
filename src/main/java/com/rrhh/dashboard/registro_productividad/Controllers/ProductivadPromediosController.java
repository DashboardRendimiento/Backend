package com.rrhh.dashboard.registro_productividad.Controllers;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.rrhh.dashboard.registro_productividad.Dtos.PromedioProductividadDTO;
import com.rrhh.dashboard.registro_productividad.Service.ProductivadPromedios;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/productividad/promedios")
@RequiredArgsConstructor
public class ProductivadPromediosController {

    private final ProductivadPromedios productividadPromedios;

    /**
     * Promedio por jornada del usuario autenticado.
     */
    @GetMapping("/me/jornada")
        @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<PromedioProductividadDTO> obtenerMiPromedioPorJornada(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate inicio,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fin) {

        return ResponseEntity.ok(
                productividadPromedios.obtenerMiPromedioPorJornada(inicio, fin)
        );
    }

    /**
     * Promedio por hora del usuario autenticado.
     */
    @GetMapping("/me/hora")
        @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<PromedioProductividadDTO> obtenerMiPromedioPorHora(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate inicio,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fin) {

        return ResponseEntity.ok(
                productividadPromedios.obtenerMiPromedioPorHora(inicio, fin)
        );
    }

    /**
     * Promedio por jornada de un empleado específico.
     */
    @GetMapping("/{empleadoId}/jornada")
    public ResponseEntity<PromedioProductividadDTO> obtenerPromedioPorJornada(
            @PathVariable Long empleadoId,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate inicio,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fin) {

        return ResponseEntity.ok(
                productividadPromedios.obtenerPromedioPorJornada(
                        empleadoId,
                        inicio,
                        fin
                )
        );
    }

    /**
     * Promedio por hora de un empleado específico.
     */
    @GetMapping("/{empleadoId}/hora")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR','SUPERADMIN')")
    public ResponseEntity<PromedioProductividadDTO> obtenerPromedioPorHora(
            @PathVariable Long empleadoId,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate inicio,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fin) {

        return ResponseEntity.ok(
                productividadPromedios.obtenerPromedioPorHora(
                        empleadoId,
                        inicio,
                        fin
                )
        );
    }
}
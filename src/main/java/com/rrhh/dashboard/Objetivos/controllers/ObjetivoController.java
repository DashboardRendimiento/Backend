package com.rrhh.dashboard.Objetivos.controllers;

import com.rrhh.dashboard.Objetivos.Entity.Objetivo;
import com.rrhh.dashboard.Objetivos.dtos.ActualizarObjetivoRequest;
import com.rrhh.dashboard.Objetivos.dtos.CrearObjetivoRequest;
import com.rrhh.dashboard.Objetivos.dtos.ObjetivoResponse;
import com.rrhh.dashboard.Objetivos.exceptions.ForbiddenObjetivoAccessException;
import com.rrhh.dashboard.Objetivos.services.ObjetivoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Las lecturas (GET) las puede hacer un rol de gestion (ve cualquier
 * empleado) o el propio empleado duenio del objetivo (ve solo lo suyo) —
 * se valida en el metodo, no con SpEL, mismo criterio que
 * AttendanceController.requireOwnRecord.
 */
@RestController
@RequestMapping("/api/objetivos")
public class ObjetivoController {

    private final ObjetivoService service;

    public ObjetivoController(ObjetivoService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPERADMIN')")
    public ResponseEntity<ObjetivoResponse> crear(@Valid @RequestBody CrearObjetivoRequest request) {
        Objetivo objetivo = service.crear(
                request.empleadoId(), request.tipo(), request.valorSemanal(), request.semanaInicio());
        return ResponseEntity.ok(ObjetivoResponse.from(objetivo));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPERADMIN')")
    public ResponseEntity<ObjetivoResponse> actualizar(@PathVariable Long id,
                                                        @Valid @RequestBody ActualizarObjetivoRequest request) {
        Objetivo objetivo = service.actualizar(id, request.valorSemanal());
        return ResponseEntity.ok(ObjetivoResponse.from(objetivo));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPERADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLEADO', 'ADMINISTRADOR', 'SUPERADMIN', 'SUPERVISOR')")
    public ResponseEntity<ObjetivoResponse> obtener(@PathVariable Long id) {
        Objetivo objetivo = service.obtener(id);
        requireOwnOrPrivileged(objetivo.getEmpleadoId());
        return ResponseEntity.ok(ObjetivoResponse.from(objetivo));
    }

    @GetMapping("/empleado/{empleadoId}")
    @PreAuthorize("hasAnyRole('EMPLEADO', 'ADMINISTRADOR', 'SUPERADMIN', 'SUPERVISOR')")
    public ResponseEntity<List<ObjetivoResponse>> listarPorEmpleado(@PathVariable Long empleadoId) {
        requireOwnOrPrivileged(empleadoId);
        List<ObjetivoResponse> response = service.listarPorEmpleado(empleadoId).stream()
                .map(ObjetivoResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    private void requireOwnOrPrivileged(Long empleadoId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean esPrivilegiado = authentication.getAuthorities().stream()
                .anyMatch(authority -> switch (authority.getAuthority()) {
                    case "ROLE_ADMINISTRADOR", "ROLE_SUPERADMIN", "ROLE_SUPERVISOR" -> true;
                    default -> false;
                });
        if (esPrivilegiado) {
            return;
        }
        Long authenticatedEmployeeId = Long.valueOf(authentication.getName());
        if (!empleadoId.equals(authenticatedEmployeeId)) {
            throw new ForbiddenObjetivoAccessException(empleadoId, authenticatedEmployeeId);
        }
    }
}

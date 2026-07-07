package com.rrhh.dashboard.Productividad.Controllers;

import com.rrhh.dashboard.Productividad.Dtos.RegistroHorarioRequest;
import com.rrhh.dashboard.Productividad.Dtos.RegistroHorarioResponse;
import com.rrhh.dashboard.Productividad.Entity.RegistroHorario;
import com.rrhh.dashboard.Productividad.Service.RegistroHorarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/productividad/registro")
public class RegistroHorarioController {

    private final RegistroHorarioService service;

    public RegistroHorarioController(RegistroHorarioService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('EMPLEADO', 'ADMINISTRADOR', 'SUPERADMIN', 'SUPERVISOR')")
    public ResponseEntity<RegistroHorarioResponse> registrar(@Valid @RequestBody RegistroHorarioRequest request) {
        RegistroHorario registro = service.registrar(
                authenticatedEmployeeId(), request.pedidosPreparados(), request.bultosPreparados());
        return ResponseEntity.ok(RegistroHorarioResponse.from(registro));
    }

    private Long authenticatedEmployeeId() {
        return Long.valueOf(
                SecurityContextHolder.getContext().getAuthentication().getName());
    }
}

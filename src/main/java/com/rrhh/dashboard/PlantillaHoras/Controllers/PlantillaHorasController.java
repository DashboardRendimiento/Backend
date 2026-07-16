package com.rrhh.dashboard.PlantillaHoras.Controllers;

import com.rrhh.dashboard.PlantillaHoras.Dtos.PlantillaHorasDTO;
import com.rrhh.dashboard.PlantillaHoras.Service.PlantillaHorasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plantilla-horas")
@RequiredArgsConstructor
public class PlantillaHorasController {

    private final PlantillaHorasService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMINISTRADOR')")
    public ResponseEntity<PlantillaHorasDTO> guardar(@RequestBody PlantillaHorasDTO dto) {
        return ResponseEntity.ok(service.guardar(dto));
    }

    @GetMapping("/empleado/{empleadoId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMINISTRADOR','SUPERVISOR')")
    public ResponseEntity<List<PlantillaHorasDTO>> obtenerPorMes(
            @PathVariable Long empleadoId,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(service.obtenerPorEmpleadoYMes(empleadoId, year, month));
    }
}

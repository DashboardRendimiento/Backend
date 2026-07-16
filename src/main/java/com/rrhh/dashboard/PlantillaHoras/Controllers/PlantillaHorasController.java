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
public class PlantillaHorasController {

    private final PlantillaHorasService service;
    public PlantillaHorasController(PlantillaHorasService service) {
        this.service = service;
    }


    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMINISTRADOR')")
    public ResponseEntity<PlantillaHorasDTO> guardar(@RequestBody PlantillaHorasDTO dto) {
        return ResponseEntity.ok(service.guardar(dto));
    }

    @GetMapping("/empleado/{empleadoId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMINISTRADOR','SUPERVISOR','EMPLEADO')")
    public ResponseEntity<List<PlantillaHorasDTO>> obtenerPorMes(
            @PathVariable Long empleadoId,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(service.obtenerPorEmpleadoYMes(empleadoId, year, month));
    }

    @GetMapping("/fecha/{fecha}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMINISTRADOR','SUPERVISOR','EMPLEADO')")
    public ResponseEntity<List<PlantillaHorasDTO>> obtenerPorFecha(@PathVariable String fecha) {
        return ResponseEntity.ok(service.obtenerPorFecha(java.time.LocalDate.parse(fecha)));
    }
}

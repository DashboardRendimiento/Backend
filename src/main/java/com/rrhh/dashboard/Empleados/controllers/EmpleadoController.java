package com.rrhh.dashboard.Empleados.controllers;

import com.rrhh.dashboard.Empleados.Entity.Empleados;
import com.rrhh.dashboard.Empleados.services.EmpleadoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/empleados") 
@RequiredArgsConstructor
public class EmpleadoController {

    private final EmpleadoService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR')")
    public ResponseEntity<List<Empleados>> listar() {
        List<Empleados> empleados = service.listar();
        if (empleados.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(empleados);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR')")
    public ResponseEntity<Empleados> obtenerPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR')")
    public ResponseEntity<Empleados> crear(@Valid @RequestBody Empleados empleado) {
        Empleados nuevoEmpleado = service.guardar(empleado);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEmpleado);
    }

    @PutMapping("/{id}")     
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR')")
    public ResponseEntity<Empleados> actualizar(
            @PathVariable Long id, 
            @Valid @RequestBody Empleados empleado) {
        Empleados actualizado = service.actualizar(id, empleado);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sector/{sector}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR')")
    public ResponseEntity<List<Empleados>> buscarPorSector(@PathVariable String sector) {
        List<Empleados> empleados = service.buscarPorSector(sector);
        return ResponseEntity.ok(empleados);
    }

    @GetMapping("/puesto/{puesto}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR')")
    public ResponseEntity<List<Empleados>> buscarPorPuesto(@PathVariable String puesto) {
        List<Empleados> empleados = service.buscarPorPuesto(puesto);
        return ResponseEntity.ok(empleados);
    }

    @GetMapping("/contar/sector/{sector}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR')")
    public ResponseEntity<Map<String, Long>> contarPorSector(@PathVariable String sector) {
        Long cantidad = service.contarPorSector(sector);
        return ResponseEntity.ok(Map.of("cantidad", cantidad));
    }

    @GetMapping("/contar/puesto/{puesto}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR')")
    public ResponseEntity<Map<String, Long>> contarPorPuesto(@PathVariable String puesto) {
        Long cantidad = service.contarPorPuesto(puesto);
        return ResponseEntity.ok(Map.of("cantidad", cantidad));
    }

    @GetMapping("/total")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR')")
    public ResponseEntity<Map<String, Long>> totalEmpleados() {
        Long total = service.totalEmpleados();
        return ResponseEntity.ok(Map.of("total", total));
    }

    @GetMapping("/buscar/dni/{dni}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR')")
    public ResponseEntity<List<Empleados>> buscarPorDni(@PathVariable Long dni) {
        List<Empleados> empleados = service.buscarPorDni(dni);
        return ResponseEntity.ok(empleados);
    }

    @GetMapping("/buscar/nombre/{nombre}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR')")
    public ResponseEntity<List<Empleados>> buscarPorNombre(@PathVariable String nombre) {
        List<Empleados> empleados = service.buscarPorNombre(nombre);
        return ResponseEntity.ok(empleados);
    }

    @GetMapping("/buscar/apellido/{apellido}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR')")
    public ResponseEntity<List<Empleados>> buscarPorApellido(@PathVariable String apellido) {
        List<Empleados> empleados = service.buscarPorApellido(apellido);
        return ResponseEntity.ok(empleados);
    }

}
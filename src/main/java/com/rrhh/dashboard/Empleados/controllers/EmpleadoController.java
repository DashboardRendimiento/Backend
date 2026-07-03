package com.rrhh.dashboard.Empleados.controllers;

import com.rrhh.dashboard.Empleados.Entity.Empleados;
import com.rrhh.dashboard.Empleados.services.EmpleadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/empleados") 
@RequiredArgsConstructor
public class EmpleadoController {

    private final EmpleadoService service;

    @GetMapping
    public ResponseEntity<List<Empleados>> listar() {
        List<Empleados> empleados = service.listar();
        return ResponseEntity.ok(empleados);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Empleados> obtenerPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Empleados> crear(@RequestBody Empleados empleado) {
        Empleados nuevoEmpleado = service.guardar(empleado);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEmpleado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Empleados> actualizar(
            @PathVariable Long id, 
            @RequestBody Empleados empleado) {
        try {
            Empleados actualizado = service.actualizar(id, empleado);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            service.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/sector/{sector}")
    public ResponseEntity<List<Empleados>> buscarPorSector(@PathVariable String sector) {
        List<Empleados> empleados = service.buscarPorSector(sector);
        if (empleados.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(empleados);
    }

    @GetMapping("/puesto/{puesto}")
    public ResponseEntity<List<Empleados>> buscarPorPuesto(@PathVariable String puesto) {
        List<Empleados> empleados = service.buscarPorPuesto(puesto);
        if (empleados.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(empleados);
    }

    @GetMapping("/contar/sector/{sector}")
    public ResponseEntity<Map<String, Long>> contarPorSector(@PathVariable String sector) {
        Long cantidad = service.contarPorSector(sector);
        return ResponseEntity.ok(Map.of("cantidad", cantidad));
    }

    @GetMapping("/contar/puesto/{puesto}")
    public ResponseEntity<Map<String, Long>> contarPorPuesto(@PathVariable String puesto) {
        Long cantidad = service.contarPorPuesto(puesto);
        return ResponseEntity.ok(Map.of("cantidad", cantidad));
    }

    @GetMapping("/total")
    public ResponseEntity<Map<String, Long>> totalEmpleados() {
        Long total = service.totalEmpleados();
        return ResponseEntity.ok(Map.of("total", total));
    }

}
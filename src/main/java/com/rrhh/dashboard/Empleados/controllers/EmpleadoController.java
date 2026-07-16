package com.rrhh.dashboard.Empleados.controllers;

import com.rrhh.dashboard.Empleados.Entity.Empleados;
import com.rrhh.dashboard.Empleados.services.EmpleadoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/empleados") 
@RequiredArgsConstructor
public class EmpleadoController {

    private final EmpleadoService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR','SUPERADMIN')")
    public ResponseEntity<List<Empleados>> listar() {
        List<Empleados> empleados = service.listar();
        if (empleados.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(empleados);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLEADO','ADMINISTRADOR','SUPERVISOR','SUPERADMIN')")
    public ResponseEntity<Empleados> obtenerPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public ResponseEntity<Empleados> obtenerMiPerfil() {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            Long empleadoId = Long.valueOf(auth.getName());
            return service.buscarPorId(empleadoId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping(value = "/me/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> subirMiFoto(@RequestParam("foto") MultipartFile foto) {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            Long empleadoId = Long.valueOf(auth.getName());
            byte[] fotoBytes = leerBytes(foto);
            if (fotoBytes != null) {
                service.actualizarFoto(empleadoId, fotoBytes);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.badRequest().build();
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * multipart/form-data en vez de JSON: ademas de los datos del empleado,
     * acepta la foto de referencia para verificacion facial (modulo
     * Asistencia) — se enrola en el alta, no hay endpoint separado para
     * cargarla despues.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<Empleados> crear(@RequestPart("empleado") Empleados empleado,
                                            @RequestPart(value = "foto", required = false) MultipartFile foto) {
        byte[] fotoBytes = leerBytes(foto);
        Empleados nuevoEmpleado = service.guardar(empleado, fotoBytes);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEmpleado);
    }

    @PutMapping("/{id}")     
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR','SUPERADMIN')")
    public ResponseEntity<Empleados> actualizar(
            @PathVariable Long id, 
            @Valid @RequestBody Empleados empleado) {
        Empleados actualizado = service.actualizar(id, empleado);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR','SUPERADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sector/{sector}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR','SUPERADMIN')")
    public ResponseEntity<List<Empleados>> buscarPorSector(@PathVariable String sector) {
        List<Empleados> empleados = service.buscarPorSector(sector);
        return ResponseEntity.ok(empleados);
    }

    @GetMapping("/puesto/{puesto}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR','SUPERADMIN')")
    public ResponseEntity<List<Empleados>> buscarPorPuesto(@PathVariable String puesto) {
        List<Empleados> empleados = service.buscarPorPuesto(puesto);
        return ResponseEntity.ok(empleados);
    }

    @GetMapping("/contar/sector/{sector}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR','SUPERADMIN')")
    public ResponseEntity<Map<String, Long>> contarPorSector(@PathVariable String sector) {
        Long cantidad = service.contarPorSector(sector);
        return ResponseEntity.ok(Map.of("cantidad", cantidad));
    }

    @GetMapping("/contar/puesto/{puesto}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR','SUPERADMIN')")
    public ResponseEntity<Map<String, Long>> contarPorPuesto(@PathVariable String puesto) {
        Long cantidad = service.contarPorPuesto(puesto);
        return ResponseEntity.ok(Map.of("cantidad", cantidad));
    }

    @GetMapping("/total")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR','SUPERADMIN')")
    public ResponseEntity<Map<String, Long>> totalEmpleados() {
        Long total = service.totalEmpleados();
        return ResponseEntity.ok(Map.of("total", total));
    }

    @GetMapping("/buscar/dni/{dni}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR','SUPERADMIN')")
    public ResponseEntity<List<Empleados>> buscarPorDni(@PathVariable Long dni) {
        List<Empleados> empleados = service.buscarPorDni(dni);
        return ResponseEntity.ok(empleados);
    }

    @GetMapping("/buscar/nombre/{nombre}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR','SUPERADMIN')")
    public ResponseEntity<List<Empleados>> buscarPorNombre(@PathVariable String nombre) {
        List<Empleados> empleados = service.buscarPorNombre(nombre);
        return ResponseEntity.ok(empleados);
    }

    @GetMapping("/buscar/apellido/{apellido}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR','SUPERADMIN')")
    public ResponseEntity<List<Empleados>> buscarPorApellido(@PathVariable String apellido) {
        List<Empleados> empleados = service.buscarPorApellido(apellido);
        return ResponseEntity.ok(empleados);
    }

    /**
     * Foto de referencia de verificacion facial — servida aparte (no en el
     * JSON del empleado) para que quien revisa un fichaje pendiente
     * (modulo Asistencia) pueda compararla a simple vista.
     */
    @GetMapping("/{id}/foto")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPERADMIN', 'SUPERVISOR')")
    public ResponseEntity<byte[]> obtenerFoto(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(Empleados::getFotoReferencia)
                .filter(foto -> foto != null && foto.length > 0)
                .map(foto -> ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(foto))
                .orElse(ResponseEntity.notFound().build());
    }

    private byte[] leerBytes(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            return null;
        }
        try {
            return archivo.getBytes();
        } catch (IOException e) {
            throw new UncheckedIOException("Error leyendo el archivo subido", e);
        }
    }

}

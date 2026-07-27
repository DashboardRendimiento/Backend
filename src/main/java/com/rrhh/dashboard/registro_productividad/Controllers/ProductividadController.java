package com.rrhh.dashboard.registro_productividad.Controllers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.rrhh.dashboard.registro_productividad.Dtos.*;
import com.rrhh.dashboard.registro_productividad.Entity.registro_productividad;
import com.rrhh.dashboard.registro_productividad.Service.ProductividadService;


import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/productividad")
public class ProductividadController {
    private static final Logger log = LoggerFactory.getLogger(ProductividadController.class);



    private final ProductividadService service;
    public ProductividadController(ProductividadService service) {
        this.service = service;
    }



    @PostMapping
    @PreAuthorize("hasAnyRole('EMPLEADO','SUPERVISOR','ADMINISTRADOR','SUPERADMIN')")
    public ResponseEntity<?> guardar(
            @RequestBody ProductividadDiariaDTO dto){


        registro_productividad productividad =
                new registro_productividad();


        productividad.setFecha(dto.getFecha());

        productividad.setPedidosPreparados(
                dto.getPedidosPreparados()
        );

        productividad.setBultosPreparados(
                dto.getBultosPreparados()
        );

        return ResponseEntity.ok(
                service.guardar(productividad, dto.getEmpleado())
        );
    }



    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR','SUPERADMIN')")
    public ResponseEntity<?> obtenerTodos(){

        return ResponseEntity.ok(
                service.obtenerTodos()
        );
    }



    @GetMapping("/empleado/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR','SUPERADMIN')")
    public ResponseEntity<?> empleado(
            @PathVariable Long id){

        return ResponseEntity.ok(
                service.obtenerPorEmpleado(id)
        );
    }



    @GetMapping("/mi-productividad")

    @PreAuthorize("hasAnyRole('EMPLEADO', 'SUPERVISOR', 'ADMINISTRADOR', 'SUPERADMIN')")
    public ResponseEntity<?> miProductividad(){

        return ResponseEntity.ok(
                service.obtenerMiProductividad()
        );
    }



    @GetMapping("/fecha")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR','SUPERADMIN')")
    public ResponseEntity<?> fecha(
            @RequestParam LocalDate fecha){

        return ResponseEntity.ok(
                service.obtenerPorFecha(fecha)
        );
    }

}

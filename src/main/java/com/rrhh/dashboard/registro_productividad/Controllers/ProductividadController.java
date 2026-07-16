package com.rrhh.dashboard.registro_productividad.Controllers;

import com.rrhh.dashboard.registro_productividad.Dtos.*;
import com.rrhh.dashboard.registro_productividad.Entity.registro_productividad;
import com.rrhh.dashboard.registro_productividad.Service.ProductividadService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/productividad")
@RequiredArgsConstructor
public class ProductividadController {


    private final ProductividadService service;


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
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR')")
    public ResponseEntity<?> obtenerTodos(){

        return ResponseEntity.ok(
                service.obtenerTodos()
        );
    }



    @GetMapping("/empleado/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR')")
    public ResponseEntity<?> empleado(
            @PathVariable Long id){

        return ResponseEntity.ok(
                service.obtenerPorEmpleado(id)
        );
    }



    @GetMapping("/mi-productividad")

    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<?> miProductividad(){

        return ResponseEntity.ok(
                service.obtenerMiProductividad()
        );
    }



    @GetMapping("/fecha")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR')")
    public ResponseEntity<?> fecha(
            @RequestParam LocalDate fecha){

        return ResponseEntity.ok(
                service.obtenerPorFecha(fecha)
        );
    }

}
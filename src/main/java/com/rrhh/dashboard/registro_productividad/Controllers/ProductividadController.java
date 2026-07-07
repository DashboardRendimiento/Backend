package com.rrhh.dashboard.registro_productividad.Controllers;

import com.rrhh.dashboard.Empleados.Entity.Empleados;
import com.rrhh.dashboard.Empleados.dtos.EmpleadoDTO;
import com.rrhh.dashboard.registro_productividad.Dtos.*;
import com.rrhh.dashboard.registro_productividad.Entity.registro_productividad;
import com.rrhh.dashboard.registro_productividad.Service.ProductividadService;

import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/productividad")
@RequiredArgsConstructor
public class ProductividadController {


    private final ProductividadService service;


    // ==========================
    // CONVERSIONES DTO
    // ==========================

    private ProductividadResponseDto toDTO(registro_productividad p) {

        ProductividadResponseDto dto = new ProductividadResponseDto();


        if (p.getEmpleado() != null) {

            Empleados e = p.getEmpleado();

            EmpleadoDTO emp = new EmpleadoDTO();

            emp.setIdEmpleado(String.valueOf(e.getId()));
            emp.setNombre(e.getNombre());
            emp.setApellido(e.getApellido());
            emp.setSector(e.getSector());
            emp.setPuesto(e.getPuesto());
            emp.setTurno(e.getTurno());

            if(e.getDni()!=null){
                emp.setDni(e.getDni().longValue());
            }

            dto.setEmpleado(emp);
        }


        dto.setFecha(p.getFecha());
        dto.setPedidosEncargados(p.getPedidosEncargados());
        dto.setPedidosPreparados(p.getPedidosPreparados());
        dto.setPedidosPendientes(p.getPedidosPendientes());
        dto.setBultosPreparados(p.getBultosPreparados());


        return dto;
    }



    private registro_productividad toEntity(ProductividadDiariaDTO dto){

        registro_productividad productividad =
                new registro_productividad();


        productividad.setFecha(dto.getFecha());
        productividad.setPedidosEncargados(dto.getPedidosEncargados());
        productividad.setPedidosPreparados(dto.getPedidosPreparados());
        productividad.setBultosPreparados(dto.getBultosPreparados());


        return productividad;
    }



    // ==========================
    // GUARDAR
    // ==========================


    @PostMapping
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<?> guardar(
            @RequestBody ProductividadDiariaDTO dto){

        registro_productividad productividad =
                toEntity(dto);


        registro_productividad guardado =
                service.guardar(productividad);


        return ResponseEntity.ok(
                toDTO(guardado)
        );
    }



    // ==========================
    // CONSULTAS GENERALES
    // ==========================


    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERADMIN')")
    public ResponseEntity<List<ProductividadResponseDto>> obtenerTodos(){

        return ResponseEntity.ok(
                service.obtenerTodos()
                        .stream()
                        .map(this::toDTO)
                        .collect(Collectors.toList())
        );
    }



    // ==========================
    // POR EMPLEADO
    // ==========================


    @GetMapping("/empleado/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERADMIN')")
    public ResponseEntity<List<ProductividadResponseDto>> obtenerEmpleado(
            @PathVariable Long id){


        return ResponseEntity.ok(
                service.obtenerPorEmpleado(id)
                        .stream()
                        .map(this::toDTO)
                        .collect(Collectors.toList())
        );
    }


    //PRODUCTIVIDAD POR TOKEN DE EMPLEADO
    @GetMapping("/empleado")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<List<ProductividadResponseDto>> miProductividad(){


        return ResponseEntity.ok(
                service.obtenerMiProductividad()
                        .stream()
                        .map(this::toDTO)
                        .collect(Collectors.toList())
        );
    }


    
    @GetMapping("/empleado/nombre/{nombre}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERADMIN')")
    public ResponseEntity<List<ProductividadResponseDto>> porNombre(
            @PathVariable String nombre){

        return ResponseEntity.ok(
                service.obtenerPorNombre(nombre)
                        .stream()
                        .map(this::toDTO)
                        .collect(Collectors.toList())
        );
    }




    // ==========================
    // FECHAS
    // ==========================


    @GetMapping("/fecha")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERADMIN')")
    public ResponseEntity<List<ProductividadResponseDto>> fecha(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fecha){


        return ResponseEntity.ok(
                service.obtenerPorFecha(fecha)
                        .stream()
                        .map(this::toDTO)
                        .collect(Collectors.toList())
        );
    }




    @GetMapping("/mi-fecha")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<List<ProductividadResponseDto>> miFecha(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fecha){


        return ResponseEntity.ok(
                service.obtenerMiProductividadPorFecha(fecha)
                        .stream()
                        .map(this::toDTO)
                        .collect(Collectors.toList())
        );
    }





    // ==========================
    // RANGO
    // ==========================


    @GetMapping("/empleado/{id}/rango")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERADMIN')")
    public ResponseEntity<List<ProductividadResponseDto>> rangoEmpleado(
            @PathVariable Long id,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate inicio,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fin){


        return ResponseEntity.ok(
                service.obtenerPorEmpleadoYRango(id,inicio,fin)
                        .stream()
                        .map(this::toDTO)
                        .collect(Collectors.toList())
        );
    }



    @GetMapping("/mi-rango")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<List<ProductividadResponseDto>> miRango(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate inicio,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fin){


        return ResponseEntity.ok(
                service.obtenerMiProductividadPorRango(inicio,fin)
                        .stream()
                        .map(this::toDTO)
                        .collect(Collectors.toList())
        );
    }




    // ==========================
    // KPI
    // ==========================


    @GetMapping("/empleado/{id}/kpi")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERADMIN')")
    public ResponseEntity<ProductividadKPIDTO> kpi(
            @PathVariable Long id){

        return ResponseEntity.ok(
                service.obtenerKPI(id)
        );
    }



    @GetMapping("/mi-kpi")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<ProductividadKPIDTO> miKpi(){

        return ResponseEntity.ok(
                service.obtenerMiKPI()
        );
    }



    @GetMapping("/kpi/global")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERADMIN')")
    public ResponseEntity<List<ProductividadEmpleadoKPI>> global(){

        return ResponseEntity.ok(
                service.obtenerKPIGlobal()
        );
    }




    // ==========================
    // PROMEDIOS
    // ==========================


    @GetMapping("/empleado/{id}/promedio/jornada")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERADMIN')")
    public ResponseEntity<PromedioProductividadDTO> promedioJornada(
            @PathVariable Long id,
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fin){

        return ResponseEntity.ok(
                service.obtenerPromedioPorJornada(id,inicio,fin)
        );
    }



    @GetMapping("/empleado/{id}/promedio/hora")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERADMIN')")
    public ResponseEntity<PromedioProductividadDTO> promedioHora(
            @PathVariable Long id,
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fin){

        return ResponseEntity.ok(
                service.obtenerPromedioPorHora(id,inicio,fin)
        );
    }



    @GetMapping("/mi-promedio/jornada")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<PromedioProductividadDTO> miPromedioJornada(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fin){

        return ResponseEntity.ok(
                service.obtenerMiPromedioPorJornada(inicio,fin)
        );
    }



    @GetMapping("/mi-promedio/hora")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<PromedioProductividadDTO> miPromedioHora(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fin){

        return ResponseEntity.ok(
                service.obtenerMiPromedioPorHora(inicio,fin)
        );
    }

}
package com.rrhh.dashboard.Productividad.Controllers;

import com.rrhh.dashboard.Empleados.Entity.Empleados;
import com.rrhh.dashboard.Empleados.dtos.EmpleadoDTO;
import com.rrhh.dashboard.Productividad.Dtos.ProductividadDiariaDTO;
import com.rrhh.dashboard.Productividad.Dtos.ProductividadEmpleadoKPI;
import com.rrhh.dashboard.Productividad.Dtos.ProductividadKPIDTO;
import com.rrhh.dashboard.Productividad.Dtos.ProductividadResponseDto;
import com.rrhh.dashboard.Productividad.Entity.ProductividadDiaria;
import com.rrhh.dashboard.Productividad.Service.ProductividadService;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/productividad")
@RequiredArgsConstructor
public class ProductividadController {

    private final ProductividadService service;
    private ProductividadResponseDto toDTO(ProductividadDiaria p) {

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
            emp.setDni(e.getDni());
            dto.setEmpleado(emp);
        }

        dto.setFecha(p.getFecha());
        dto.setPedidosEncargados(p.getPedidosEncargados());
        dto.setPedidosPreparados(p.getPedidosPreparados());
        dto.setPedidosPendientes(p.getPedidosPendientes());
        dto.setBultosPreparados(p.getBultosPreparados());

        return dto;
    }

    private ProductividadDiaria toEntity(ProductividadDiariaDTO dto) {

        ProductividadDiaria p = new ProductividadDiaria();

        if (dto.getEmpleado() != null) {
            Empleados empleado = new Empleados();
            empleado.setId(dto.getEmpleado());
            p.setEmpleado(empleado);
        }

        p.setFecha(dto.getFecha());
        p.setPedidosEncargados(dto.getPedidosEncargados());
        p.setPedidosPreparados(dto.getPedidosPreparados());
        p.setPedidosPendientes(dto.getPedidosPendientes());
        p.setBultosPreparados(dto.getBultosPreparados());

        return p;
    }

    @PostMapping
    public ResponseEntity<ProductividadResponseDto> guardar(
            @RequestBody ProductividadDiariaDTO dto) {

        ProductividadDiaria guardado = service.guardar(toEntity(dto));

        return ResponseEntity.ok(toDTO(guardado));
    }

    @GetMapping
    public ResponseEntity<List<ProductividadResponseDto>> obtenerTodos() {

        List<ProductividadResponseDto> lista = service.obtenerTodos()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/empleado/{empleadoId}")
    public ResponseEntity<List<ProductividadResponseDto>> porEmpleado(
            @PathVariable Long empleadoId) {

        List<ProductividadResponseDto> lista = service.obtenerPorEmpleado(empleadoId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(lista);
    }

@GetMapping("/empleado/nombre/{nombre}")
public ResponseEntity<List<ProductividadResponseDto>> porNombre(
        @PathVariable String nombre) {

    List<ProductividadResponseDto> lista = service.obtenerPorNombre(nombre)
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());

    return ResponseEntity.ok(lista);
}
    @GetMapping("/fecha")
    public ResponseEntity<List<ProductividadResponseDto>> porFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        List<ProductividadResponseDto> lista = service.obtenerPorFecha(fecha)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(lista);
    }

    // =========================
    // KPI PEDIDOS POR USUARIO
    // =========================
    @GetMapping("/empleado/{empleadoId}/kpi")
    public ResponseEntity<ProductividadKPIDTO> obtenerKPI(@PathVariable Long empleadoId) {

        return ResponseEntity.ok(service.obtenerKPI(empleadoId));
    }
    //===================
    //KPI PEDIDOS GLOBAL
    //===================
    @GetMapping("/kpi/global")
    public ResponseEntity<List<ProductividadEmpleadoKPI>> obtenerKPIGlobal() {
        return ResponseEntity.ok(service.obtenerKPIGlobal());
    }
}

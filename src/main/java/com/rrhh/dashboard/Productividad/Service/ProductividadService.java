package com.rrhh.dashboard.Productividad.Service;


import com.rrhh.dashboard.Empleados.Entity.Empleados;
import com.rrhh.dashboard.Empleados.services.EmpleadoService;
import com.rrhh.dashboard.Productividad.Dtos.ProductividadEmpleadoKPI;
import com.rrhh.dashboard.Productividad.Dtos.ProductividadKPIDTO;
import com.rrhh.dashboard.Productividad.Entity.ProductividadDiaria;
import com.rrhh.dashboard.Productividad.repository.ProductividadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductividadService {

    private final ProductividadRepository repository;
    private final EmpleadoService empleadosService; 

    // GUARDAR / ACTUALIZAR
    public ProductividadDiaria guardar(ProductividadDiaria productividad) {

        if (productividad.getEmpleado() != null && productividad.getEmpleado().getId() != null) {

            Empleados empleado = empleadosService.buscarPorId(productividad.getEmpleado().getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Empleado no encontrado con id: " + productividad.getEmpleado().getId()));

            productividad.setEmpleado(empleado);
        }
        // Calcular pendientes
        if (productividad.getPedidosEncargados() != null &&
            productividad.getPedidosPreparados() != null) {
            productividad.setPedidosPendientes(
                productividad.getPedidosEncargados() - productividad.getPedidosPreparados()
            );
        } else {
            productividad.setPedidosPendientes(0);
        }

        return repository.save(productividad);
    }

   
    public List<ProductividadDiaria> obtenerTodos() {
        return repository.findAll();
    }

    public List<ProductividadDiaria> obtenerPorEmpleado(Long empleadoId) {
        return repository.findByEmpleadoId(empleadoId);
    }

    public List<ProductividadDiaria> obtenerPorNombre(String nombre) {
        return repository.findByEmpleado_Nombre(nombre);
    }
    public List<ProductividadDiaria> obtenerPorFecha(LocalDate fecha) {
        return repository.findByFecha(fecha);
    }

    public List<ProductividadDiaria> obtenerPorEmpleadoYFecha(Long empleadoId, LocalDate fecha) {
        return repository.findByEmpleadoIdAndFecha(empleadoId, fecha);
    }

    public List<ProductividadDiaria> obtenerPorEmpleadoYRango(Long empleadoId, LocalDate inicio, LocalDate fin) {
        return repository.findByEmpleadoIdAndFechaBetween(empleadoId, inicio, fin);
    }
    public ProductividadKPIDTO obtenerKPI(Long empleadoId) {

                List<ProductividadDiaria> data = repository.findByEmpleadoId(empleadoId);

                ProductividadKPIDTO kpi = new ProductividadKPIDTO();

                kpi.setTotalPedidos(
                        data.stream().mapToInt(ProductividadDiaria::getPedidosPreparados).sum()
                );

                kpi.setTotalBultos(
                        data.stream().mapToInt(ProductividadDiaria::getBultosPreparados).sum()
                );

                kpi.setTotalPendientes(
                        data.stream().mapToInt(ProductividadDiaria::getPedidosPendientes).sum()
                );

                return kpi;
    }
    public List<ProductividadEmpleadoKPI> obtenerKPIGlobal() {

    return repository.findAll()
            .stream()
            .collect(Collectors.groupingBy(ProductividadDiaria::getEmpleado))
            .entrySet()
            .stream()
            .map(entry -> {

                Empleados e = entry.getKey();
                List<ProductividadDiaria> data = entry.getValue();

                ProductividadEmpleadoKPI kpi = new ProductividadEmpleadoKPI();
                kpi.setEmpleadoId(e.getId());
                kpi.setNombre(e.getNombre());

                kpi.setTotalPedidos(
                        data.stream().mapToInt(ProductividadDiaria::getPedidosPreparados).sum()
                );

                kpi.setTotalBultos(
                        data.stream().mapToInt(ProductividadDiaria::getBultosPreparados).sum()
                );

                kpi.setTotalPendientes(
                        data.stream().mapToInt(ProductividadDiaria::getPedidosPendientes).sum()
                );

                return kpi;
            })
            .toList();
}
}
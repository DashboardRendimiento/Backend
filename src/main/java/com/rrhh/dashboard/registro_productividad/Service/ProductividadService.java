package com.rrhh.dashboard.registro_productividad.Service;

import com.rrhh.dashboard.Empleados.Entity.Empleados;
import com.rrhh.dashboard.Empleados.services.EmpleadoService;
import com.rrhh.dashboard.registro_productividad.Entity.registro_productividad;
import com.rrhh.dashboard.registro_productividad.repository.ProductividadRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
@Service
@RequiredArgsConstructor
public class ProductividadService {

    private final ProductividadRepository repository;
    private final EmpleadoService empleadosService;


    private Empleados obtenerEmpleadoAutenticado() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        Long empleadoId = Long.valueOf(authentication.getName());

        return empleadosService.buscarPorId(empleadoId)
                .orElseThrow(() ->
                        new RuntimeException("Empleado no encontrado con id: " + empleadoId));
    }


    // ==========================
    // GUARDAR / ACTUALIZAR
    // ==========================

public registro_productividad guardar(registro_productividad productividad) {
    Empleados empleado = obtenerEmpleadoAutenticado();
    productividad.setEmpleado(empleado);
    
    if (productividad.getAsistencia() == null || 
        productividad.getAsistencia().getId() == null) {
        throw new RuntimeException("Debe enviarse el id de asistencia para registrar productividad");
    }
    return repository.save(productividad);
}
    // ==========================
    // CONSULTAS
    // ==========================

    public List<registro_productividad> obtenerTodos() {
        return repository.findAll();
    }

    public List<registro_productividad> obtenerPorEmpleado(Long empleadoId) {
        return repository.findByEmpleadoId(empleadoId);
    }

    public List<registro_productividad> obtenerMiProductividad() {

        return repository.findByEmpleadoId(
                obtenerEmpleadoAutenticado().getId());
    }

    public List<registro_productividad> obtenerPorNombre(String nombre) {
        return repository.findByEmpleado_Nombre(nombre);
    }

    public List<registro_productividad> obtenerPorFecha(LocalDate fecha) {
        return repository.findByFecha(fecha);
    }

    public List<registro_productividad> obtenerPorEmpleadoYFecha(Long empleadoId,
                                                                 LocalDate fecha) {

        return repository.findByEmpleadoIdAndFecha(empleadoId, fecha);
    }
  
    public List<registro_productividad> obtenerMiProductividadPorFecha(LocalDate fecha) {

        return repository.findByEmpleadoIdAndFecha(
                obtenerEmpleadoAutenticado().getId(),
                fecha);
    }

    public List<registro_productividad> obtenerPorEmpleadoYRango(Long empleadoId,
                                                                 LocalDate inicio,
                                                                 LocalDate fin) {

        return repository.findByEmpleadoIdAndFechaBetween(
                empleadoId,
                inicio,
                fin);
    }

}

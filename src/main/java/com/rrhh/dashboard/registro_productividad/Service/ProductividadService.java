package com.rrhh.dashboard.registro_productividad.Service;

import com.rrhh.dashboard.Asistencia.Entity.AttendanceRecord;
import com.rrhh.dashboard.Asistencia.Repository.AttendanceRecordRepository;
import com.rrhh.dashboard.Empleados.Entity.Empleados;
import com.rrhh.dashboard.Empleados.services.EmpleadoService;
import com.rrhh.dashboard.registro_productividad.Entity.registro_productividad;
import com.rrhh.dashboard.registro_productividad.repository.ProductividadRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductividadService {

    private final ProductividadRepository repository;
    private final EmpleadoService empleadosService;
    private final AttendanceRecordRepository attendanceRepository;

    private Empleados obtenerEmpleadoAutenticado() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        Long empleadoId = Long.valueOf(authentication.getName());

        return empleadosService.buscarPorId(empleadoId)
                .orElseThrow(() ->
                        new RuntimeException("Empleado no encontrado con id: " + empleadoId));
    }

    private AttendanceRecord obtenerAsistenciaActiva(Empleados empleado) {

        return attendanceRepository
                .findFirstByEmployeeIdAndClockOutAtIsNullOrderByClockInAtDesc(
                        empleado.getId())
                .orElseThrow(() ->
                        new RuntimeException("No se encontró una asistencia activa"));
    }

    // ==========================
    // GUARDAR / ACTUALIZAR
    // ==========================
    public registro_productividad guardar(registro_productividad productividad) {

        Empleados empleado = obtenerEmpleadoAutenticado();
        productividad.setEmpleado(empleado);

        AttendanceRecord asistenciaActiva =
                obtenerAsistenciaActiva(empleado);

        productividad.setAsistencia(asistenciaActiva);

        productividad.setFecha(LocalDate.now());
        productividad.setFechaHora(LocalDateTime.now());

        registro_productividad guardado =
                repository.save(productividad);

     

        return guardado;
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

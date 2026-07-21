package com.rrhh.dashboard.registro_productividad.Service;

import com.rrhh.dashboard.Asistencia.Entity.AttendanceRecord;
import com.rrhh.dashboard.Asistencia.Repository.AttendanceRecordRepository;
import com.rrhh.dashboard.Asistencia.exceptions.NoOpenAttendanceRecordException;
import com.rrhh.dashboard.Empleados.Entity.Empleados;
import com.rrhh.dashboard.Empleados.services.EmpleadoService;
import com.rrhh.dashboard.registro_productividad.Entity.registro_productividad;
import com.rrhh.dashboard.registro_productividad.events.ProductividadRegistradaEvent;
import com.rrhh.dashboard.registro_productividad.repository.ProductividadRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final ApplicationEventPublisher eventPublisher;

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
    public registro_productividad guardar(registro_productividad productividad, Long dtoEmpleadoId) {
        Empleados autenticado = obtenerEmpleadoAutenticado();
        Empleados empleadoAsignado;

        if (dtoEmpleadoId != null && !autenticado.getId().equals(dtoEmpleadoId)) {
            String role = autenticado.getRole().name();
            if (role.equals("EMPLEADO") || role.equals("SUPERVISOR")) {
                throw new org.springframework.security.access.AccessDeniedException("Un " + role.toLowerCase() + " solo puede registrar su propia productividad");
            }
            empleadoAsignado = empleadosService.buscarPorId(dtoEmpleadoId)
                    .orElseThrow(() -> new RuntimeException("Empleado no encontrado con id: " + dtoEmpleadoId));
        } else {
            empleadoAsignado = autenticado;
        }

        productividad.setEmpleado(empleadoAsignado);
        productividad.setFechaHora(LocalDateTime.now());

        AttendanceRecord asistenciaAbierta = attendanceRecordRepository
                .findFirstByEmployeeIdAndClockOutAtIsNullOrderByClockInAtDesc(empleadoAsignado.getId())
                .orElseThrow(() -> new NoOpenAttendanceRecordException(empleadoAsignado.getId()));
        productividad.setAsistencia(asistenciaAbierta);

        registro_productividad guardado = repository.save(productividad);
        eventPublisher.publishEvent(new ProductividadRegistradaEvent(empleadoAsignado.getId()));
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

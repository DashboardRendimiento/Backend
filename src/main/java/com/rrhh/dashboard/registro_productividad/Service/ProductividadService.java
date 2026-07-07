package com.rrhh.dashboard.registro_productividad.Service;

import com.rrhh.dashboard.Asistencia.Entity.AttendanceRecord;
import com.rrhh.dashboard.Asistencia.Repository.AttendanceRecordRepository;
import com.rrhh.dashboard.Empleados.Entity.Empleados;
import com.rrhh.dashboard.Empleados.services.EmpleadoService;
import com.rrhh.dashboard.registro_productividad.Dtos.ProductividadEmpleadoKPI;
import com.rrhh.dashboard.registro_productividad.Dtos.ProductividadKPIDTO;
import com.rrhh.dashboard.registro_productividad.Dtos.PromedioProductividadDTO;
import com.rrhh.dashboard.registro_productividad.Entity.registro_productividad;
import com.rrhh.dashboard.registro_productividad.repository.ProductividadRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
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
    
    if (productividad.getPedidosEncargados() != null &&
        productividad.getPedidosPreparados() != null) {
        
        productividad.setPedidosPendientes(
            productividad.getPedidosEncargados() - 
            productividad.getPedidosPreparados()
        );
    } else {
        productividad.setPedidosPendientes(0);
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

    /**
     * Devuelve únicamente la productividad del usuario autenticado.
     */
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

    /**
     * Devuelve únicamente la productividad del usuario autenticado para una fecha.
     */
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

    /**
     * Devuelve únicamente la productividad del usuario autenticado para un rango.
     */
    public List<registro_productividad> obtenerMiProductividadPorRango(LocalDate inicio,
                                                                       LocalDate fin) {

        return repository.findByEmpleadoIdAndFechaBetween(
                obtenerEmpleadoAutenticado().getId(),
                inicio,
                fin);
    }

    public ProductividadKPIDTO obtenerKPI(Long empleadoId) {

        List<registro_productividad> data =
                repository.findByEmpleadoId(empleadoId);

        ProductividadKPIDTO kpi = new ProductividadKPIDTO();

        kpi.setTotalPedidos(
                data.stream()
                        .mapToInt(registro_productividad::getPedidosPreparados)
                        .sum());

        kpi.setTotalBultos(
                data.stream()
                        .mapToInt(registro_productividad::getBultosPreparados)
                        .sum());

        kpi.setTotalPendientes(
                data.stream()
                        .mapToInt(registro_productividad::getPedidosPendientes)
                        .sum());

        return kpi;
    }
    public PromedioProductividadDTO obtenerPromedioPorJornada(Long empleadoId, LocalDate inicio, LocalDate fin) {
        List<registro_productividad> registros = repository.findByEmpleadoIdAndFechaBetween(empleadoId, inicio, fin);
        
        if (registros.isEmpty()) {
            return new PromedioProductividadDTO(0.0, 0.0, 0);
        }
        
        Map<LocalDate, List<registro_productividad>> registrosPorFecha = registros.stream()
                .collect(Collectors.groupingBy(registro_productividad::getFecha));
        
        int totalJornadas = registrosPorFecha.size();
        
        int totalPedidos = registros.stream()
                .mapToInt(registro_productividad::getPedidosPreparados)
                .sum();
        
        int totalBultos = registros.stream()
                .mapToInt(registro_productividad::getBultosPreparados)
                .sum();
        
        double promedioPedidosPorJornada = totalJornadas > 0 ? (double) totalPedidos / totalJornadas : 0.0;
        double promedioBultosPorJornada = totalJornadas > 0 ? (double) totalBultos / totalJornadas : 0.0;
        
        return new PromedioProductividadDTO(
                promedioPedidosPorJornada,
                promedioBultosPorJornada,
                totalJornadas
        );
    }

    /**
     * Obtiene el promedio de pedidos y bultos por hora para un empleado en un rango de fechas
     */
    public PromedioProductividadDTO obtenerPromedioPorHora(Long empleadoId, LocalDate inicio, LocalDate fin) {
        List<registro_productividad> registros = repository.findByEmpleadoIdAndFechaBetween(empleadoId, inicio, fin);
        
        if (registros.isEmpty()) {
            return new PromedioProductividadDTO(0.0, 0.0, 0);
        }
        
        // Obtener las asistencias del empleado en el rango de fechas
        Instant inicioInstant = inicio.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant finInstant = fin.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant();
        
        List<AttendanceRecord> asistencias = attendanceRepository.findByEmployeeIdAndClockInAtBetween(
                empleadoId, inicioInstant, finInstant);
        
        // Calcular horas totales trabajadas
        long totalHoras = asistencias.stream()
                .mapToLong(this::calcularHorasTrabajadas)
                .sum();
        
        if (totalHoras == 0) {
            return new PromedioProductividadDTO(0.0, 0.0, 0);
        }
        
        int totalPedidos = registros.stream()
                .mapToInt(registro_productividad::getPedidosPreparados)
                .sum();
        
        int totalBultos = registros.stream()
                .mapToInt(registro_productividad::getBultosPreparados)
                .sum();
        
        double promedioPedidosPorHora = (double) totalPedidos / totalHoras;
        double promedioBultosPorHora = (double) totalBultos / totalHoras;
        
        return new PromedioProductividadDTO(
                promedioPedidosPorHora,
                promedioBultosPorHora,
                totalHoras
        );
    }

    /**
     * Calcula las horas trabajadas en una asistencia (diferencia entre clockOutAt y clockInAt)
     */
    private long calcularHorasTrabajadas(AttendanceRecord asistencia) {
        if (asistencia.getClockOutAt() == null) {
            return Duration.between(asistencia.getClockInAt(), Instant.now()).toHours();
        }
        return Duration.between(asistencia.getClockInAt(), asistencia.getClockOutAt()).toHours();
    }

    public PromedioProductividadDTO obtenerMiPromedioPorJornada(LocalDate inicio, LocalDate fin) {
        return obtenerPromedioPorJornada(obtenerEmpleadoAutenticado().getId(), inicio, fin);
    }

    public PromedioProductividadDTO obtenerMiPromedioPorHora(LocalDate inicio, LocalDate fin) {
        return obtenerPromedioPorHora(obtenerEmpleadoAutenticado().getId(), inicio, fin);
    }
    /**
     * KPI del usuario autenticado.
     */
    public ProductividadKPIDTO obtenerMiKPI() {

        return obtenerKPI(obtenerEmpleadoAutenticado().getId());
    }

    public List<ProductividadEmpleadoKPI> obtenerKPIGlobal() {

        return repository.findAll()
                .stream()
                .collect(Collectors.groupingBy(registro_productividad::getEmpleado))
                .entrySet()
                .stream()
                .map(entry -> {

                    Empleados e = entry.getKey();
                    List<registro_productividad> data = entry.getValue();

                    ProductividadEmpleadoKPI kpi =
                            new ProductividadEmpleadoKPI();

                    kpi.setEmpleadoId(e.getId());
                    kpi.setNombre(e.getNombre());

                    kpi.setTotalPedidos(
                            data.stream()
                                    .mapToInt(registro_productividad::getPedidosPreparados)
                                    .sum());

                    kpi.setTotalBultos(
                            data.stream()
                                    .mapToInt(registro_productividad::getBultosPreparados)
                                    .sum());

                    kpi.setTotalPendientes(
                            data.stream()
                                    .mapToInt(registro_productividad::getPedidosPendientes)
                                    .sum());

                    return kpi;
                })
                .toList();
    }
}
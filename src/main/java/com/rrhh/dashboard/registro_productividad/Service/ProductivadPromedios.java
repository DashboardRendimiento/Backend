package com.rrhh.dashboard.registro_productividad.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rrhh.dashboard.Asistencia.Entity.AttendanceRecord;
import com.rrhh.dashboard.Asistencia.Repository.AttendanceRecordRepository;
import com.rrhh.dashboard.Empleados.Entity.Empleados;
import com.rrhh.dashboard.Empleados.services.EmpleadoService;
import com.rrhh.dashboard.registro_productividad.Dtos.PromedioProductividadDTO;
import com.rrhh.dashboard.registro_productividad.Entity.registro_productividad;
import com.rrhh.dashboard.registro_productividad.repository.ProductividadRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProductivadPromedios {
    private final ProductividadRepository repository;
    private final EmpleadoService empleadosService;
    private final AttendanceRecordRepository attendanceRepository;

    private Empleados obtenerEmpleadoAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long empleadoId = Long.valueOf(authentication.getName());
        return empleadosService.buscarPorId(empleadoId)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con id: " + empleadoId));
    }

    public PromedioProductividadDTO obtenerPromedioPorJornada(Long empleadoId, LocalDate inicio, LocalDate fin) {
        // Valores por defecto
        if (inicio == null) {
            inicio = LocalDate.now().minusDays(30);
        }
        if (fin == null) {
            fin = LocalDate.now();
        }

        log.info("Obteniendo promedio por jornada - Empleado: {}, Inicio: {}, Fin: {}", empleadoId, inicio, fin);

        List<registro_productividad> registros = repository.findByEmpleadoIdAndFechaBetween(empleadoId, inicio, fin);
        
        log.info("Registros encontrados: {}", registros.size());
        
        if (registros.isEmpty()) {
            return new PromedioProductividadDTO(0.0, 0.0, 0);
        }
        
        // Calcular totales
        int totalPedidos = registros.stream()
                .mapToInt(r -> r.getPedidosPreparados() != null ? r.getPedidosPreparados() : 0)
                .sum();
        
        int totalBultos = registros.stream()
                .mapToInt(r -> r.getBultosPreparados() != null ? r.getBultosPreparados() : 0)
                .sum();
        
        // Contar días únicos con registros
        long totalJornadas = registros.stream()
                .map(registro_productividad::getFecha)
                .distinct()
                .count();
        
        double promedioPedidos = totalJornadas > 0 ? (double) totalPedidos / totalJornadas : 0.0;
        double promedioBultos = totalJornadas > 0 ? (double) totalBultos / totalJornadas : 0.0;
        
        log.info("Resultado - Pedidos: {}, Bultos: {}, Jornadas: {}, PromPedidos: {}, PromBultos: {}", 
                totalPedidos, totalBultos, totalJornadas, promedioPedidos, promedioBultos);
        
        return new PromedioProductividadDTO(
                Math.round(promedioPedidos * 100.0) / 100.0,
                Math.round(promedioBultos * 100.0) / 100.0,
                totalJornadas
        );
    }

    public PromedioProductividadDTO obtenerPromedioPorHora(Long empleadoId, LocalDateTime inicio, LocalDateTime fin) {
        // Valores por defecto
        if (inicio == null) {
            inicio = LocalDate.now().minusDays(30).atStartOfDay();
        }
        if (fin == null) {
            fin = LocalDateTime.now();
        }

        log.info("Obteniendo promedio por hora - Empleado: {}, Inicio: {}, Fin: {}", empleadoId, inicio, fin);

        LocalDate fechaInicio = inicio.toLocalDate();
        LocalDate fechaFin = fin.toLocalDate();

        // Obtener registros de productividad
        List<registro_productividad> registros = repository.findByEmpleadoIdAndFechaBetween(
                empleadoId, fechaInicio, fechaFin);

        log.info("Registros encontrados: {}", registros.size());

        if (registros.isEmpty()) {
            return new PromedioProductividadDTO(0.0, 0.0, 0);
        }

        // Calcular totales
        int totalPedidos = registros.stream()
                .mapToInt(r -> r.getPedidosPreparados() != null ? r.getPedidosPreparados() : 0)
                .sum();

        int totalBultos = registros.stream()
                .mapToInt(r -> r.getBultosPreparados() != null ? r.getBultosPreparados() : 0)
                .sum();

        log.info("Totales - Pedidos: {}, Bultos: {}", totalPedidos, totalBultos);

        // Obtener asistencias en el período
        Instant inicioInstant = inicio.toInstant(ZoneOffset.UTC);
        Instant finInstant = fin.toInstant(ZoneOffset.UTC);

        List<AttendanceRecord> asistencias = attendanceRepository.findByEmployeeIdAndClockInAtBetween(
                empleadoId, inicioInstant, finInstant);

        log.info("Asistencias encontradas: {}", asistencias.size());

        // Calcular horas totales trabajadas
        long totalHoras = asistencias.stream()
                .mapToLong(this::calcularHorasTrabajadas)
                .sum();

        log.info("Horas totales calculadas: {}", totalHoras);

        // Si no hay asistencias, estimar 8 horas por día con registros
        if (totalHoras == 0 && !registros.isEmpty()) {
            long diasConRegistros = registros.stream()
                    .map(registro_productividad::getFecha)
                    .distinct()
                    .count();
            totalHoras = diasConRegistros * 8;
            log.info("Horas estimadas (sin asistencias): {} ({} días * 8 horas)", totalHoras, diasConRegistros);
        }

        // Calcular promedios por hora
        double promedioPedidosPorHora = 0.0;
        double promedioBultosPorHora = 0.0;

        if (totalHoras > 0) {
            promedioPedidosPorHora = (double) totalPedidos / totalHoras;
            promedioBultosPorHora = (double) totalBultos / totalHoras;
            log.info("Promedios por hora - Pedidos: {}, Bultos: {}", promedioPedidosPorHora, promedioBultosPorHora);
        } else {
            log.warn("Total de horas es 0, no se pueden calcular promedios por hora");
        }

        return new PromedioProductividadDTO(
                Math.round(promedioPedidosPorHora * 100.0) / 100.0,
                Math.round(promedioBultosPorHora * 100.0) / 100.0,
                totalHoras
        );
    }

    private long calcularHorasTrabajadas(AttendanceRecord asistencia) {
        if (asistencia.getClockOutAt() == null) {
            long horas = Duration.between(asistencia.getClockInAt(), Instant.now()).toHours();
            log.debug("Asistencia sin clockOut - Horas: {}", horas);
            return horas;
        }
        long horas = Duration.between(asistencia.getClockInAt(), asistencia.getClockOutAt()).toHours();
        log.debug("Asistencia completa - Horas: {}", horas);
        return horas;
    }

    public PromedioProductividadDTO obtenerMiPromedioPorJornada(LocalDate inicio, LocalDate fin) {
        Empleados empleado = obtenerEmpleadoAutenticado();
        log.info("Usuario autenticado: {}", empleado.getId());
        return obtenerPromedioPorJornada(empleado.getId(), inicio, fin);
    }

    public PromedioProductividadDTO obtenerMiPromedioPorHora(LocalDate inicio, LocalDate fin) {
        Empleados empleado = obtenerEmpleadoAutenticado();
        log.info("Usuario autenticado: {}", empleado.getId());
        
        LocalDateTime inicioDateTime = inicio != null ? inicio.atStartOfDay() : LocalDate.now().minusDays(30).atStartOfDay();
        LocalDateTime finDateTime = fin != null ? fin.atTime(23, 59, 59) : LocalDateTime.now();
        
        return obtenerPromedioPorHora(empleado.getId(), inicioDateTime, finDateTime);
    }
}
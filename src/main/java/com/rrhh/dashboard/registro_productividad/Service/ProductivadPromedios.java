package com.rrhh.dashboard.registro_productividad.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.rrhh.dashboard.Asistencia.Entity.AttendanceRecord;
import com.rrhh.dashboard.Asistencia.Repository.AttendanceRecordRepository;
import com.rrhh.dashboard.Empleados.Entity.Empleados;
import com.rrhh.dashboard.Empleados.services.EmpleadoService;
import com.rrhh.dashboard.registro_productividad.Dtos.PromedioProductividadDTO;
import com.rrhh.dashboard.registro_productividad.Entity.registro_productividad;
import com.rrhh.dashboard.registro_productividad.repository.ProductividadRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductivadPromedios {
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

   
    public PromedioProductividadDTO obtenerPromedioPorHora(Long empleadoId, LocalDate inicio, LocalDate fin) {
        List<registro_productividad> registros = repository.findByEmpleadoIdAndFechaBetween(empleadoId, inicio, fin);
        
        if (registros.isEmpty()) {
            return new PromedioProductividadDTO(0.0, 0.0, 0);
        }
        
        Instant inicioInstant = inicio.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant finInstant = fin.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant();
        
        List<AttendanceRecord> asistencias = attendanceRepository.findByEmployeeIdAndClockInAtBetween(
                empleadoId, inicioInstant, finInstant);
        
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
}



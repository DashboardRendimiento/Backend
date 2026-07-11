package com.rrhh.dashboard.registro_productividad.Service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.rrhh.dashboard.Empleados.Entity.Empleados;
import com.rrhh.dashboard.Empleados.services.EmpleadoService;
import com.rrhh.dashboard.Objetivos.Entity.Objetivo;
import com.rrhh.dashboard.Objetivos.Entity.TipoObjetivo;
import com.rrhh.dashboard.Objetivos.services.ObjetivoService;
import com.rrhh.dashboard.registro_productividad.Dtos.ProductividadKPIDTO;
import com.rrhh.dashboard.registro_productividad.Entity.registro_productividad;
import com.rrhh.dashboard.registro_productividad.repository.ProductividadRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductividadKPIService {
    private final ProductividadRepository repository;
    private final EmpleadoService empleadosService;
    private final ObjetivoService objetivoService;

    private Empleados obtenerEmpleadoAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long empleadoId = Long.valueOf(authentication.getName());
        return empleadosService.buscarPorId(empleadoId)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con id: " + empleadoId));
    }

    public ProductividadKPIDTO obtenerMiKPI() {
        return obtenerKPI(obtenerEmpleadoAutenticado().getId());
    }

    public ProductividadKPIDTO obtenerKPI(Long empleadoId) {
        log.info("=== CALCULANDO KPI para empleado: {} ===", empleadoId);

        List<registro_productividad> allRegistros = repository.findByEmpleadoId(empleadoId);
        log.info("Total registros encontrados: {}", allRegistros.size());

        LocalDate hoy = LocalDate.now();
        LocalDate inicioSemana = hoy.with(java.time.DayOfWeek.MONDAY);
        LocalDate finSemana = hoy.with(java.time.DayOfWeek.SUNDAY);

        log.info("Semana actual: {} - {}", inicioSemana, finSemana);

        int totalPedidos = allRegistros.stream()
                .mapToInt(registro_productividad::getPedidosPreparados)
                .sum();

        int totalBultos = allRegistros.stream()
                .mapToInt(registro_productividad::getBultosPreparados)
                .sum();

        log.info("Total histórico - Pedidos: {}, Bultos: {}", totalPedidos, totalBultos);

        // ==========================
        // CALCULAR PEDIDOS DE LA SEMANA
        // ==========================
        int pedidosSemana = allRegistros.stream()
                .filter(reg -> {
                    LocalDate fecha = reg.getFecha();
                    if (fecha == null) {
                        return true;
                    }
                    return !fecha.isBefore(inicioSemana) && !fecha.isAfter(finSemana);
                })
                .mapToInt(registro_productividad::getPedidosPreparados)
                .sum();

        int bultosSemana = allRegistros.stream()
                .filter(reg -> {
                    LocalDate fecha = reg.getFecha();
                    if (fecha == null) {
                        return true;
                    }
                    return !fecha.isBefore(inicioSemana) && !fecha.isAfter(finSemana);
                })
                .mapToInt(registro_productividad::getBultosPreparados)
                .sum();

        log.info("Pedidos de la semana: {}", pedidosSemana);
        log.info("Bultos de la semana: {}", bultosSemana);

        allRegistros.stream()
                .filter(reg -> {
                    LocalDate fecha = reg.getFecha();
                    if (fecha == null) return true;
                    return !fecha.isBefore(inicioSemana) && !fecha.isAfter(finSemana);
                })
                .forEach(reg -> {
                    log.info("  Registro semana - ID: {}, Fecha: {}, Pedidos: {}, Bultos: {}",
                        reg.getId(), reg.getFecha(), reg.getPedidosPreparados(), reg.getBultosPreparados());
                });

        Objetivo objetivoPedidos = objetivoService.obtenerObjetivoActual(
                empleadoId, TipoObjetivo.PEDIDOS, hoy);

        ProductividadKPIDTO kpi = new ProductividadKPIDTO();

        kpi.setTotalPedidos(totalPedidos);
        kpi.setTotalBultos(totalBultos);

        if (objetivoPedidos != null) {
            double objetivo = objetivoPedidos.getValorSemanal();
            log.info("Objetivo semanal encontrado: {}", objetivo);

            double pendiente = Math.max(0, objetivo - pedidosSemana);

            double porcentajeCumplimiento = 0.0;
            if (objetivo > 0) {
                porcentajeCumplimiento = (pedidosSemana / objetivo) * 100;
                porcentajeCumplimiento = Math.min(100, porcentajeCumplimiento);
                porcentajeCumplimiento = Math.round(porcentajeCumplimiento * 100.0) / 100.0;
            }


            kpi.setObjetivoPedidos(objetivo);
            kpi.setPedidosPendientesObjetivo(pendiente);
            kpi.setPorcentajeCumplimiento(porcentajeCumplimiento);

        } else {
            kpi.setObjetivoPedidos(0.0);
            kpi.setPedidosPendientesObjetivo(0.0);
            kpi.setPorcentajeCumplimiento(0.0);
        }

        log.info("=== FIN CÁLCULO KPI ===");
        return kpi;
    }

    // ==========================
    // MÉTODOS AUXILIARES PARA DEPURACIÓN
    // ==========================

    public void limpiarRegistrosConFechaNula() {
        List<registro_productividad> todos = repository.findAll();
        int contador = 0;

        for (registro_productividad reg : todos) {
            if (reg.getFecha() == null) {
                log.warn("Registro con ID {} tiene fecha nula", reg.getId());
                reg.setFecha(LocalDate.now());
                repository.save(reg);
                contador++;
            }
        }

        log.info("Se corrigieron {} registros con fecha nula", contador);
    }

    public void debugRegistrosEmpleado(Long empleadoId) {
        List<registro_productividad> registros = repository.findByEmpleadoId(empleadoId);

        log.info("=== REGISTROS DEL EMPLEADO {} ===", empleadoId);
        log.info("Total registros: {}", registros.size());

        if (registros.isEmpty()) {
            log.warn("⚠️ No hay registros para el empleado {}", empleadoId);
            return;
        }

        // Mostrar todos los registros
        registros.forEach(reg -> {
            log.info("ID: {}, Fecha: {}, Pedidos: {}, Bultos: {}",
                reg.getId(),
                reg.getFecha(),
                reg.getPedidosPreparados(),
                reg.getBultosPreparados());
        });

        int totalPedidos = registros.stream()
                .mapToInt(registro_productividad::getPedidosPreparados)
                .sum();

        int totalBultos = registros.stream()
                .mapToInt(registro_productividad::getBultosPreparados)
                .sum();

        log.info("TOTALES HISTÓRICOS:");
        log.info("  - Total Pedidos: {}", totalPedidos);
        log.info("  - Total Bultos: {}", totalBultos);

        long registrosConFechaNula = registros.stream()
                .filter(reg -> reg.getFecha() == null)
                .count();

        log.info("Registros con fecha nula: {}", registrosConFechaNula);

        LocalDate hoy = LocalDate.now();
        LocalDate inicioSemana = hoy.with(java.time.DayOfWeek.MONDAY);
        LocalDate finSemana = hoy.with(java.time.DayOfWeek.SUNDAY);

        int pedidosSemana = registros.stream()
                .filter(reg -> {
                    LocalDate fecha = reg.getFecha();
                    if (fecha == null) return true;
                    return !fecha.isBefore(inicioSemana) && !fecha.isAfter(finSemana);
                })
                .mapToInt(registro_productividad::getPedidosPreparados)
                .sum();

        int bultosSemana = registros.stream()
                .filter(reg -> {
                    LocalDate fecha = reg.getFecha();
                    if (fecha == null) return true;
                    return !fecha.isBefore(inicioSemana) && !fecha.isAfter(finSemana);
                })
                .mapToInt(registro_productividad::getBultosPreparados)
                .sum();

        log.info("TOTALES DE LA SEMANA ACTUAL:");
        log.info("  - Pedidos semana: {}", pedidosSemana);
        log.info("  - Bultos semana: {}", bultosSemana);

        Objetivo objetivo = objetivoService.obtenerObjetivoActual(
                empleadoId, TipoObjetivo.PEDIDOS, hoy);

        if (objetivo != null) {
            double pendiente = Math.max(0, objetivo.getValorSemanal() - pedidosSemana);
            double porcentaje = 0.0;
            if (objetivo.getValorSemanal() > 0) {
                porcentaje = Math.min(100, (pedidosSemana / objetivo.getValorSemanal()) * 100);
                porcentaje = Math.round(porcentaje * 100.0) / 100.0;
            }

            log.info("OBJETIVO Y KPI:");
            log.info("  - Objetivo semanal: {}", objetivo.getValorSemanal());
            log.info("  - Pedidos pendientes: {}", pendiente);
            log.info("  - Porcentaje cumplimiento: {}%", porcentaje);
        } else {
            log.warn("  - No hay objetivo definido para este empleado");
        }
    }
}

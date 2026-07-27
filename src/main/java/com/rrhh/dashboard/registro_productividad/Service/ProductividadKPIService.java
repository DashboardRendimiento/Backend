package com.rrhh.dashboard.registro_productividad.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rrhh.dashboard.Empleados.Entity.Empleados;
import com.rrhh.dashboard.Empleados.services.EmpleadoService;
import com.rrhh.dashboard.Objetivos.Entity.Objetivo;
import com.rrhh.dashboard.Objetivos.Entity.TipoObjetivo;
import com.rrhh.dashboard.Objetivos.services.ObjetivoService;
import com.rrhh.dashboard.registro_productividad.Dtos.KpiMensual;
import com.rrhh.dashboard.registro_productividad.Dtos.KpiSemanal;
import com.rrhh.dashboard.registro_productividad.Dtos.ProductividadKPIDTO;
import com.rrhh.dashboard.registro_productividad.Entity.registro_productividad;
import com.rrhh.dashboard.registro_productividad.repository.ProductividadRepository;


@Service
@Transactional
public class ProductividadKPIService {
    private static final Logger log = LoggerFactory.getLogger(ProductividadKPIService.class);

    private final ProductividadRepository repository;
    private final EmpleadoService empleadosService;
    private final ObjetivoService objetivoService;
    public ProductividadKPIService(ProductividadRepository repository, EmpleadoService empleadosService, ObjetivoService objetivoService) {
        this.repository = repository;
        this.empleadosService = empleadosService;
        this.objetivoService = objetivoService;
    }


    // ==================================================
    // OBTENER EMPLEADO AUTENTICADO
    // ==================================================

    public Empleados obtenerEmpleadoAutenticado() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        Long empleadoId = Long.valueOf(authentication.getName());

        return empleadosService.buscarPorId(empleadoId)
                .orElseThrow(() -> new RuntimeException(
                        "Empleado no encontrado con id: " + empleadoId
                ));
    }

    // ==================================================
    // KPI USUARIO LOGUEADO
    // ==================================================

    public ProductividadKPIDTO obtenerMiKPI() {
        return obtenerKPI(obtenerEmpleadoAutenticado().getId());
    }

    // ==================================================
    // KPI POR EMPLEADO (COMPLETO)
    // ==================================================

    public ProductividadKPIDTO obtenerKPI(Long empleadoId) {
        ProductividadKPIDTO kpi = new ProductividadKPIDTO();
        KpiSemanal kpiSemanal = new KpiSemanal();
        KpiMensual kpiMensual = new KpiMensual();

        LocalDate hoy = LocalDate.now();

        // ==================================================
        // 1. KPI HISTORIAL (TOTAL)
        // ==================================================

        List<registro_productividad> data = repository.findByEmpleadoId(empleadoId);
        int totalPedidos = data.stream()
                .mapToInt(registro_productividad::getPedidosPreparados)
                .sum();
        kpi.setTotalPedidos(totalPedidos);

        // ==================================================
        // 2. KPI DEL DÍA ACTUAL
        // ==================================================

        obtenerKPIDiario(kpi, empleadoId, hoy);

        // ==================================================
        // 3. KPI DE LA SEMANA
        // ==================================================

        obtenerKPISemanal(kpiSemanal, empleadoId, hoy);

        // ==================================================
        // 4. KPI DEL MES (SOLO TOTAL DE PEDIDOS)
        // ==================================================

        obtenerKPIMensual(kpiMensual, empleadoId, hoy);

        return kpi;
    }

    // ==================================================
    // MÉTODO PARA KPI DIARIO
    // ==================================================

    public void obtenerKPIDiario(ProductividadKPIDTO kpi, Long empleadoId, LocalDate hoy) {
        List<registro_productividad> registrosDia = repository
                .findByEmpleadoIdAndFecha(empleadoId, hoy);
        
        LocalDate fechaUtilizada = hoy;

        // Si hoy no tiene registros, buscar el último registro anterior
        if (registrosDia.isEmpty()) {
            Optional<registro_productividad> ultimoRegistro = repository
                    .findTopByEmpleadoIdAndFechaLessThanEqualOrderByFechaDesc(
                            empleadoId, hoy.minusDays(1)
                    );
            if (ultimoRegistro.isPresent()) {
                registro_productividad registro = ultimoRegistro.get();
                registrosDia = List.of(registro);
                fechaUtilizada = registro.getFecha();
            }
        }

        int pedidosDia = registrosDia.stream()
                .mapToInt(registro_productividad::getPedidosPreparados)
                .sum();

        kpi.setPedidosDia(pedidosDia);
        kpi.setFechaUtilizada(fechaUtilizada);

        // Obtener objetivo
        Objetivo objetivoPedidos = objetivoService.obtenerObjetivoActual(
                empleadoId, TipoObjetivo.PEDIDOS, fechaUtilizada
        );

        if (objetivoPedidos != null && objetivoPedidos.getValorSemanal() > 0) {
            double objetivoDiario = objetivoPedidos.getValorDiario();
            
            kpi.setObjetivoPedidos(objetivoDiario);
            
            double pendiente = Math.max(0, objetivoDiario - pedidosDia);
            kpi.setPedidosPendientesObjetivo(pendiente);

            if (objetivoDiario > 0) {
                double porcentaje = (pedidosDia * 100.0) / objetivoDiario;
                // Limitar el porcentaje a 100% (no puede superar el 100%)
                kpi.setPorcentajeCumplimiento(Math.min(porcentaje, 100.0));
            }
        } else {
            log.warn("No se encontró objetivo para el empleado {} en la fecha {}", empleadoId, fechaUtilizada);
            kpi.setObjetivoPedidos(0);
            kpi.setPedidosPendientesObjetivo(0);
            kpi.setPorcentajeCumplimiento(0);
        }
    }

    // ==================================================
    // KPI SEMANAL
    // ==================================================

    public void obtenerKPISemanal(KpiSemanal kpi, Long empleadoId, LocalDate hoy) {
        LocalDate inicioSemana = hoy.with(DayOfWeek.MONDAY);
        LocalDate finSemana = hoy.with(DayOfWeek.SUNDAY);

        // Obtener registros de la semana
        List<registro_productividad> registrosSemana = repository
                .findByEmpleadoIdAndFechaBetween(empleadoId, inicioSemana, finSemana);

        int pedidosSemana = registrosSemana.stream()
                .mapToInt(registro_productividad::getPedidosPreparados)
                .sum();

        kpi.setPedidosSemana(pedidosSemana);
        kpi.setInicioSemana(inicioSemana);
        kpi.setFinSemana(finSemana);

        // Obtener objetivo semanal
        Objetivo objetivoPedidos = objetivoService.obtenerObjetivoActual(
                empleadoId, TipoObjetivo.PEDIDOS, hoy
        );

        if (objetivoPedidos != null && objetivoPedidos.getValorSemanal() > 0) {
            double objetivoSemanal = objetivoPedidos.getValorSemanal();
            kpi.setObjetivoSemanal(objetivoSemanal);

            if (objetivoSemanal > 0) {
                double porcentaje = (pedidosSemana * 100.0) / objetivoSemanal;
                // Limitar el porcentaje a 100%
                kpi.setPorcentajeCumplimientoSemanal(Math.min(porcentaje, 100.0));
            }
        } else {
            log.warn("No se encontró objetivo semanal para el empleado {}", empleadoId);
            kpi.setObjetivoSemanal(0);
            kpi.setPorcentajeCumplimientoSemanal(0);
        }
    }

    // ==================================================
    // KPI MENSUAL (SOLO TOTAL DE PEDIDOS)
    // ==================================================

    public void obtenerKPIMensual(KpiMensual kpiMensual, Long empleadoId, LocalDate hoy) {
        LocalDate inicioMes = hoy.withDayOfMonth(1);
        LocalDate finMes = hoy.withDayOfMonth(hoy.lengthOfMonth());

        // Obtener registros del mes
        List<registro_productividad> registrosMes = repository
                .findByEmpleadoIdAndFechaBetween(empleadoId, inicioMes, finMes);

        int pedidosMes = registrosMes.stream()
                .mapToInt(registro_productividad::getPedidosPreparados)
                .sum();

        kpiMensual.setPedidosMes(pedidosMes);
        kpiMensual.setInicioMes(inicioMes);
        kpiMensual.setFinMes(finMes);
    }

    // ==================================================
    // MÉTODOS CRUD
    // ==================================================


    public ProductividadKPIDTO obtenerMiProductividad() {
        return obtenerMiKPI();
    }

    public KpiSemanal obtenerKPISoloSemanal(Long empleadoId) {
        KpiSemanal kpiSemanal = new KpiSemanal();
        LocalDate hoy = LocalDate.now();
        obtenerKPISemanal(kpiSemanal, empleadoId, hoy);
        return kpiSemanal;
    }

    public KpiMensual obtenerKPISoloMensual(Long empleadoId) {
        KpiMensual kpiMensual = new KpiMensual();
        LocalDate hoy = LocalDate.now();
        obtenerKPIMensual(kpiMensual, empleadoId, hoy);
        return kpiMensual;
    }
}
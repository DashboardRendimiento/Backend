package com.rrhh.dashboard.registro_productividad.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rrhh.dashboard.Empleados.Entity.Empleados;
import com.rrhh.dashboard.Empleados.services.EmpleadoService;
import com.rrhh.dashboard.Objetivos.Entity.Objetivo;
import com.rrhh.dashboard.Objetivos.Entity.TipoObjetivo;
import com.rrhh.dashboard.Objetivos.services.ObjetivoService;
import com.rrhh.dashboard.registro_productividad.Dtos.ProductividadKPIDTO;
import com.rrhh.dashboard.registro_productividad.Entity.registro_productividad;
import com.rrhh.dashboard.registro_productividad.repository.ProductividadRepository;

import com.rrhh.dashboard.registro_productividad.Service.ProductivadPromedios;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
public class ProductividadKPIService {
    private static final Logger log = LoggerFactory.getLogger(ProductividadKPIService.class);

     private final ProductividadRepository repository;
    private final EmpleadoService empleadosService;
    private final ObjetivoService objetivoService;
    private final ProductivadPromedios promediosService;
    public ProductividadKPIService(ProductividadRepository repository, EmpleadoService empleadosService, ObjetivoService objetivoService, ProductivadPromedios promediosService) {
        this.repository = repository;
        this.empleadosService = empleadosService;
        this.objetivoService = objetivoService;
        this.promediosService = promediosService;
    }



    private Empleados obtenerEmpleadoAutenticado() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        Long empleadoId = Long.valueOf(authentication.getName());

        return empleadosService.buscarPorId(empleadoId)
                .orElseThrow(() ->
                        new RuntimeException("Empleado no encontrado con id: " + empleadoId));
    }

    //KPIS POR USUARIO AUTENTICADO
    public ProductividadKPIDTO obtenerMiKPI() {

        return obtenerKPI(obtenerEmpleadoAutenticado().getId());
    }


    public ProductividadKPIDTO obtenerKPI(Long empleadoId) {
        // ==========================
        // KPI HISTÃ“RIAL
        // ==========================

        List<registro_productividad> data =
                repository.findByEmpleadoId(empleadoId);
        ProductividadKPIDTO kpi =
                new ProductividadKPIDTO();
        int totalPedidos =
                data.stream()
                        .mapToInt(
                            r -> r.getPedidosPreparados() == null ? 0 : r.getPedidosPreparados()
                        )
                        .sum();
        int totalBultos =
                data.stream()
                        .mapToInt(
                            r -> r.getBultosPreparados() == null ? 0 : r.getBultosPreparados()
                        )
                        .sum();

        kpi.setTotalPedidos(totalPedidos);
        
        data.stream()
            .map(registro_productividad::getFechaCarga)
            .filter(java.util.Objects::nonNull)
            .max(java.time.LocalDateTime::compareTo)
            .ifPresent(kpi::setUltimaHoraCarga);

        kpi.setTotalBultos(totalBultos);

        // ==========================
        // PROMEDIOS (HistÃ³ricos o del mes actual)
        // ==========================
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);
        LocalDate finMes = hoy.withDayOfMonth(hoy.lengthOfMonth());
        
        // Usamos los Ãºltimos 30 dÃ­as para un promedio mÃ¡s representativo
        LocalDate inicio30Dias = hoy.minusDays(30);

        try {
            var promediosHora = promediosService.obtenerPromedioPorHora(empleadoId, inicio30Dias, hoy);
            kpi.setPedidosPorHora(promediosHora.getPromedioPedidos());
            kpi.setBultosPorHora(promediosHora.getPromedioBultos());

            var promediosJornada = promediosService.obtenerPromedioPorJornada(empleadoId, inicio30Dias, hoy);
            kpi.setPromedioPedidosPorJornada(promediosJornada.getPromedioPedidos());
            kpi.setPromedioBultosPorJornada(promediosJornada.getPromedioBultos());
        } catch(Exception e) {
            kpi.setPedidosPorHora(0.0);
            kpi.setBultosPorHora(0.0);
            kpi.setPromedioPedidosPorJornada(0.0);
            kpi.setPromedioBultosPorJornada(0.0);
        }

        // ==========================
        // OBJETIVO SEMANAL
        // ==========================

        LocalDate inicioSemana =
                obtenerInicioSemana(hoy);

        LocalDate finSemana =
                obtenerFinSemana(hoy);

        List<registro_productividad> registrosSemana =
                repository.findByEmpleadoIdAndFechaBetween(
                        empleadoId,
                        inicioSemana,
                        finSemana
                );
        int pedidosSemana =
                registrosSemana.stream()
                        .mapToInt(
                            r -> r.getPedidosPreparados() == null ? 0 : r.getPedidosPreparados()
                        )
                        .sum();

        Objetivo objetivoPedidos =
                objetivoService.obtenerObjetivoActual(
                        empleadoId,
                        TipoObjetivo.PEDIDOS,
                        hoy
                );

        if(objetivoPedidos != null){
            double objetivo =
                    objetivoPedidos.getValorSemanal();

            double pendiente =
                    objetivo - pedidosSemana;

            if(pendiente < 0){
                pendiente = 0;
            }

            kpi.setObjetivoPedidos(
                    objetivo
            );

            kpi.setPedidosPendientesObjetivo(
                    pendiente
            );

            kpi.setPorcentajeCumplimiento(
                    (pedidosSemana / objetivo) * 100
            );
        }

        return kpi;
    }

//==============================================
//=========METODOS AUXILIARES===================
//===============================================
private LocalDate obtenerInicioSemana(LocalDate fecha) {
    return fecha.minusDays(
            fecha.getDayOfWeek().getValue() - 1
    );
}


private LocalDate obtenerFinSemana(LocalDate fecha) {
    return obtenerInicioSemana(fecha)
            .plusDays(5);
}
}


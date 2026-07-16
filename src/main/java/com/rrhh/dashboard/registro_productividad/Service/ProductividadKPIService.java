package com.rrhh.dashboard.registro_productividad.Service;

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
import com.rrhh.dashboard.registro_productividad.Dtos.ProductividadKPIDTO;
import com.rrhh.dashboard.registro_productividad.Entity.registro_productividad;
import com.rrhh.dashboard.registro_productividad.repository.ProductividadRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductividadKPIService {


    private final ProductividadRepository repository;
    private final EmpleadoService empleadosService;
    private final ObjetivoService objetivoService;
    private final ProductivadPromedios promediosService;


    // ==================================================
    // OBTENER EMPLEADO AUTENTICADO
    // ==================================================

    private Empleados obtenerEmpleadoAutenticado() {

        Authentication authentication =
                SecurityContextHolder
                .getContext()
                .getAuthentication();


        Long empleadoId =
                Long.valueOf(authentication.getName());


        return empleadosService.buscarPorId(empleadoId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Empleado no encontrado con id: "
                                + empleadoId
                        )
                );
    }



    // ==================================================
    // KPI USUARIO LOGUEADO
    // ==================================================

    public ProductividadKPIDTO obtenerMiKPI() {

        return obtenerKPI(
                obtenerEmpleadoAutenticado().getId()
        );
    }



    // ==================================================
    // KPI POR EMPLEADO
    // ==================================================

    public ProductividadKPIDTO obtenerKPI(Long empleadoId) {


        ProductividadKPIDTO kpi =
                new ProductividadKPIDTO();



        // ==================================================
        // KPI HISTORIAL
        // ==================================================

        List<registro_productividad> data =
                repository.findByEmpleadoId(empleadoId);



        int totalPedidos =
                data.stream()
                .mapToInt(
                    registro_productividad::getPedidosPreparados
                )
                .sum();



        kpi.setTotalPedidos(totalPedidos);



        LocalDate hoy =
                LocalDate.now();



        // ==================================================
        // KPI DEL DÍA ACTUAL
        // SI NO EXISTE BUSCA EL ÚLTIMO DÍA DISPONIBLE
        // ==================================================

        List<registro_productividad> registrosDia =
                repository.findByEmpleadoIdAndFecha(
                        empleadoId,
                        hoy
                );



        LocalDate fechaUtilizada = hoy;



        /*
         * Si hoy no tiene registros,
         * buscar el último registro anterior
         */
        if (registrosDia.isEmpty()) {


            Optional<registro_productividad> ultimoRegistro =

                    repository
                    .findTopByEmpleadoIdAndFechaLessThanEqualOrderByFechaDesc(
                            empleadoId,
                            hoy.minusDays(1)
                    );



            if (ultimoRegistro.isPresent()) {


                registro_productividad registro =
                        ultimoRegistro.get();


                registrosDia =
                        List.of(registro);


                fechaUtilizada =
                        registro.getFecha();

            }

        }



        // ==================================================
        // PEDIDOS DEL DÍA UTILIZADO
        // ==================================================

        int pedidosDia =

                registrosDia.stream()
                .mapToInt(
                    registro_productividad::getPedidosPreparados
                )
                .sum();



        /*
         * Opcional:
         * si agregas fechaReferencia en el DTO
         */
        // kpi.setFechaReferencia(fechaUtilizada);



        // ==================================================
        // OBJETIVO DIARIO
        // ==================================================

        Objetivo objetivoPedidos =

                objetivoService.obtenerObjetivoActual(
                        empleadoId,
                        TipoObjetivo.PEDIDOS,
                        fechaUtilizada
                );



        if (objetivoPedidos != null) {


            double objetivoDiario =
                    objetivoPedidos.getValorDiario();



            double pendiente =

                    objetivoDiario - pedidosDia;



            if (pendiente < 0) {
                pendiente = 0;
            }



            kpi.setObjetivoPedidos(
                    objetivoDiario
            );



            kpi.setPedidosPendientesObjetivo(
                    pendiente
            );



            if (objetivoDiario > 0) {


                double porcentaje =

                        (pedidosDia * 100.0)
                        /
                        objetivoDiario;



                kpi.setPorcentajeCumplimiento(
                        porcentaje
                );

            }

        }



        return kpi;
    }




    // ==================================================
    // METODOS AUXILIARES
    // ==================================================

    private LocalDate obtenerInicioSemana(LocalDate fecha) {

        return fecha.minusDays(
                fecha.getDayOfWeek()
                .getValue() - 1
        );
    }


}
package com.rrhh.dashboard.Objetivos.services;

import com.rrhh.dashboard.Objetivos.Entity.Objetivo;
import com.rrhh.dashboard.Objetivos.Entity.TipoObjetivo;
import com.rrhh.dashboard.Objetivos.Repository.ObjetivoRepository;
import com.rrhh.dashboard.Objetivos.dtos.ObjetivoProgresoResponse;
import com.rrhh.dashboard.Objetivos.dtos.ObjetivoResponse;
import com.rrhh.dashboard.Objetivos.exceptions.DuplicateObjetivoException;
import com.rrhh.dashboard.Objetivos.exceptions.ObjetivoNotFoundException;
import com.rrhh.dashboard.Productividad.Service.RegistroHorarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Objetivos semanales por empleado (R-007 de Horarios es un modulo
 * distinto: este es "cuanto tiene que producir", no "cuando trabaja").
 */
@Service
public class ObjetivoService {

    private final ObjetivoRepository repository;
    private final RegistroHorarioService registroHorarioService;

    public ObjetivoService(ObjetivoRepository repository, RegistroHorarioService registroHorarioService) {
        this.repository = repository;
        this.registroHorarioService = registroHorarioService;
    }

    @Transactional
    public Objetivo crear(Long empleadoId, TipoObjetivo tipo, Double valorSemanal, LocalDate semanaInicio) {
        LocalDate semana = semanaInicio != null ? semanaInicio : lunesDeEstaSemana();
        if (repository.existsByEmpleadoIdAndTipoAndSemanaInicio(empleadoId, tipo, semana)) {
            throw new DuplicateObjetivoException(empleadoId, tipo, semana);
        }
        return repository.save(new Objetivo(empleadoId, tipo, valorSemanal, semana));
    }

    @Transactional
    public Objetivo actualizar(Long id, Double nuevoValorSemanal) {
        Objetivo objetivo = obtener(id);
        objetivo.setValorSemanal(nuevoValorSemanal);
        return objetivo;
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ObjetivoNotFoundException(id);
        }
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Objetivo obtener(Long id) {
        return repository.findById(id).orElseThrow(() -> new ObjetivoNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Objetivo> listarPorEmpleado(Long empleadoId) {
        return repository.findByEmpleadoId(empleadoId);
    }

    /**
     * Cruce automatico con Productividad: solo para tipo PEDIDOS, que es el
     * unico con una fuente de datos real hoy (RegistroHorario). Para DINERO
     * no hay de donde sacar "cuanto lleva cargado" todavia.
     */
    @Transactional(readOnly = true)
    public ObjetivoProgresoResponse calcularProgreso(Long id) {
        Objetivo objetivo = obtener(id);

        if (objetivo.getTipo() != TipoObjetivo.PEDIDOS) {
            return new ObjetivoProgresoResponse(ObjetivoResponse.from(objetivo), null, null, null, null);
        }

        LocalDate hoy = LocalDate.now();
        double cargadoHoy = registroHorarioService.totalPedidosDelDia(objetivo.getEmpleadoId(), hoy);
        double pendienteHoy = Math.max(0, objetivo.getValorDiario() - cargadoHoy);

        double cargadoSemana = diasDesde(objetivo.getSemanaInicio(), hoy).stream()
                .mapToDouble(dia -> registroHorarioService.totalPedidosDelDia(objetivo.getEmpleadoId(), dia))
                .sum();
        double pendienteSemana = Math.max(0, objetivo.getValorSemanal() - cargadoSemana);

        return new ObjetivoProgresoResponse(
                ObjetivoResponse.from(objetivo), cargadoHoy, pendienteHoy, cargadoSemana, pendienteSemana);
    }

    private LocalDate lunesDeEstaSemana() {
        LocalDate hoy = LocalDate.now();
        return hoy.minusDays(hoy.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue());
    }

    private List<LocalDate> diasDesde(LocalDate semanaInicio, LocalDate hasta) {
        LocalDate limite = hasta.isBefore(semanaInicio) ? semanaInicio : hasta;
        return semanaInicio.datesUntil(limite.plusDays(1)).collect(Collectors.toList());
    }
}

package com.rrhh.dashboard.Objetivos.services;

import com.rrhh.dashboard.Objetivos.Entity.Objetivo;
import com.rrhh.dashboard.Objetivos.Entity.TipoObjetivo;
import com.rrhh.dashboard.Objetivos.Repository.ObjetivoRepository;
import com.rrhh.dashboard.Objetivos.exceptions.DuplicateObjetivoException;
import com.rrhh.dashboard.Objetivos.exceptions.ObjetivoNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Objetivos semanales por empleado (R-007 de Horarios es un modulo
 * distinto: este es "cuanto tiene que producir", no "cuando trabaja").
 */
@Service
public class ObjetivoService {

    private final ObjetivoRepository repository;

    public ObjetivoService(ObjetivoRepository repository) {
        this.repository = repository;
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
        public Objetivo obtenerObjetivoActual(
            Long empleadoId,
            TipoObjetivo tipo,
            LocalDate fecha
    ){
        LocalDate inicioSemana = fecha.minusDays(fecha.getDayOfWeek().getValue() - 1);

        // Obtener la lista
        Optional<Objetivo> objetivos = repository
            .findByEmpleadoIdAndTipoAndSemanaInicio(
                empleadoId,
                tipo,
                inicioSemana
            );

        // Si está vacía, retornar null
        if (objetivos.isEmpty()) {
            return null;
        }

        // Si hay duplicados, tomar el más reciente (mayor ID)
        return objetivos.stream()
            .max((o1, o2) -> o1.getId().compareTo(o2.getId()))
            .orElse(null);
    }

    private LocalDate lunesDeEstaSemana() {
        LocalDate hoy = LocalDate.now();
        return hoy.minusDays(hoy.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue());
    }

}

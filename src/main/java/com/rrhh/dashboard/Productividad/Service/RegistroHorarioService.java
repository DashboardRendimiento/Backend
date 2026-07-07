package com.rrhh.dashboard.Productividad.Service;

import com.rrhh.dashboard.Productividad.Entity.RegistroHorario;
import com.rrhh.dashboard.Productividad.exceptions.YaRegistradoEnEstaHoraException;
import com.rrhh.dashboard.Productividad.repository.RegistroHorarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class RegistroHorarioService {

    private final RegistroHorarioRepository repository;

    public RegistroHorarioService(RegistroHorarioRepository repository) {
        this.repository = repository;
    }

    /**
     * Registra la carga horaria del empleado autenticado. Falla si ya hay un
     * registro suyo dentro de la misma hora calendario (decision local: evita
     * cargas duplicadas, un empleado carga a lo sumo una vez por hora).
     */
    @Transactional
    public RegistroHorario registrar(Long empleadoId, Integer pedidosPreparados, Integer bultosPreparados) {
        Instant ahora = Instant.now();
        Instant inicioHora = ahora.truncatedTo(ChronoUnit.HOURS);
        Instant finHora = inicioHora.plus(1, ChronoUnit.HOURS);

        if (repository.existsByEmpleadoIdAndRegistradoEnBetween(empleadoId, inicioHora, finHora)) {
            throw new YaRegistradoEnEstaHoraException(empleadoId);
        }

        return repository.save(new RegistroHorario(empleadoId, pedidosPreparados, bultosPreparados, ahora));
    }

    @Transactional(readOnly = true)
    public List<RegistroHorario> listarPorEmpleado(Long empleadoId) {
        return repository.findByEmpleadoId(empleadoId);
    }

    /**
     * Suma de pedidosPreparados del empleado en la fecha indicada (zona
     * horaria del servidor) — lo usa Objetivos para el cruce automatico con
     * lo cargado en Productividad.
     */
    @Transactional(readOnly = true)
    public int totalPedidosDelDia(Long empleadoId, LocalDate fecha) {
        Instant inicioDia = fecha.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant finDia = fecha.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        return repository.findByEmpleadoIdAndRegistradoEnBetween(empleadoId, inicioDia, finDia).stream()
                .mapToInt(RegistroHorario::getPedidosPreparados)
                .sum();
    }
}

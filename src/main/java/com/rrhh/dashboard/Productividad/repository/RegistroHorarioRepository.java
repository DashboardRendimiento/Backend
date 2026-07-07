package com.rrhh.dashboard.Productividad.repository;

import com.rrhh.dashboard.Productividad.Entity.RegistroHorario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface RegistroHorarioRepository extends JpaRepository<RegistroHorario, Long> {

    boolean existsByEmpleadoIdAndRegistradoEnBetween(Long empleadoId, Instant desde, Instant hasta);

    List<RegistroHorario> findByEmpleadoIdAndRegistradoEnBetween(Long empleadoId, Instant desde, Instant hasta);

    List<RegistroHorario> findByEmpleadoId(Long empleadoId);
}

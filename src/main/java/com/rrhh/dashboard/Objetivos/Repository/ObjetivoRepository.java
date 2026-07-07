package com.rrhh.dashboard.Objetivos.Repository;

import com.rrhh.dashboard.Objetivos.Entity.Objetivo;
import com.rrhh.dashboard.Objetivos.Entity.TipoObjetivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ObjetivoRepository extends JpaRepository<Objetivo, Long> {

    List<Objetivo> findByEmpleadoId(Long empleadoId);

    Optional<Objetivo> findByEmpleadoIdAndSemanaInicio(Long empleadoId, LocalDate semanaInicio);

    boolean existsByEmpleadoIdAndTipoAndSemanaInicio(Long empleadoId, TipoObjetivo tipo, LocalDate semanaInicio);
}

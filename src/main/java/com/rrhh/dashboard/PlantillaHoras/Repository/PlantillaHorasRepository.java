package com.rrhh.dashboard.PlantillaHoras.Repository;

import com.rrhh.dashboard.PlantillaHoras.Entity.PlantillaHoras;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlantillaHorasRepository extends JpaRepository<PlantillaHoras, Long> {
    Optional<PlantillaHoras> findByEmpleadoIdAndFecha(Long empleadoId, LocalDate fecha);
    List<PlantillaHoras> findByEmpleadoIdAndFechaBetween(Long empleadoId, LocalDate startDate, LocalDate endDate);
    List<PlantillaHoras> findByFecha(LocalDate fecha);
}

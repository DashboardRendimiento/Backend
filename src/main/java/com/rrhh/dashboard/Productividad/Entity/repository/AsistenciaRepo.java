package com.rrhh.dashboard.Productividad.Entity.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rrhh.dashboard.Empleados.Entity.Empleados;
import com.rrhh.dashboard.Productividad.Entity.AsistenciaDiaria;



@Repository
public interface AsistenciaRepo extends JpaRepository<AsistenciaDiaria, Long> {
    // Solo métodos de consulta personalizados si los necesitas
    List<Empleados> findByIdEmpleado(String idEmpleado);
    List<AsistenciaDiaria> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);
}
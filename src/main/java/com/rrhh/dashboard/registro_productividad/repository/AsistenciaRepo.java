package com.rrhh.dashboard.registro_productividad.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rrhh.dashboard.Empleados.Entity.Empleados;
import com.rrhh.dashboard.registro_productividad.Entity.AsistenciaDiaria;



@Repository
public interface AsistenciaRepo extends JpaRepository<AsistenciaDiaria, Long> {
    List<Empleados> findByIdEmpleado(String idEmpleado);
    List<AsistenciaDiaria> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);
}
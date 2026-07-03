package com.rrhh.dashboard.Productividad.repository;

import com.rrhh.dashboard.Productividad.Entity.ProductividadDiaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProductividadRepository extends JpaRepository<ProductividadDiaria, Long> {

    @EntityGraph(attributePaths = {"empleado"})
    List<ProductividadDiaria> findByEmpleadoId(Long empleadoId);
    
    @EntityGraph(attributePaths = {"empleado"})
    List<ProductividadDiaria> findByEmpleadoIdAndFecha(Long empleadoId, LocalDate fecha);
    
    @EntityGraph(attributePaths = {"empleado"})
    List<ProductividadDiaria> findByEmpleadoIdAndFechaBetween(
            Long empleadoId,
            LocalDate inicio,
            LocalDate fin
    );

    @EntityGraph(attributePaths = {"empleado"})
    List<ProductividadDiaria> findByEmpleado_Nombre(String nombre);
    
    @EntityGraph(attributePaths = {"empleado"})
    List<ProductividadDiaria> findByEmpleado_NombreAndFecha(String nombre, LocalDate fecha);
    
    @EntityGraph(attributePaths = {"empleado"})
    List<ProductividadDiaria> findByEmpleado_NombreAndFechaBetween(
            String nombre,
            LocalDate inicio,
            LocalDate fin
    );
    
    @EntityGraph(attributePaths = {"empleado"})
    List<ProductividadDiaria> findByFecha(LocalDate fecha);
    
    @EntityGraph(attributePaths = {"empleado"})
    List<ProductividadDiaria> findByFechaBetween(LocalDate inicio, LocalDate fin);
}
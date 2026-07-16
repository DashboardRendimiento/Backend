package com.rrhh.dashboard.registro_productividad.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import com.rrhh.dashboard.registro_productividad.Entity.registro_productividad;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductividadRepository extends JpaRepository<registro_productividad, Long> {

    @EntityGraph(attributePaths = {"empleado"})
    List<registro_productividad> findByEmpleadoId(Long empleadoId);
    
    @EntityGraph(attributePaths = {"empleado"})
    List<registro_productividad> findByEmpleadoIdAndFecha(Long empleadoId, LocalDate fecha);
    
    @EntityGraph(attributePaths = {"empleado"})
    List<registro_productividad> findByEmpleadoIdAndFechaBetween(
            Long empleadoId,
            LocalDate inicio,
            LocalDate fin
    );

    @EntityGraph(attributePaths = {"empleado"})
    List<registro_productividad> findByEmpleado_Nombre(String nombre);
    
    @EntityGraph(attributePaths = {"empleado"})
    List<registro_productividad> findByEmpleado_NombreAndFecha(String nombre, LocalDate fecha);
    
    @EntityGraph(attributePaths = {"empleado"})
    List<registro_productividad> findByEmpleado_NombreAndFechaBetween(
            String nombre,
            LocalDate inicio,
            LocalDate fin
    );
    
    @EntityGraph(attributePaths = {"empleado"})
    List<registro_productividad> findByFecha(LocalDate fecha);
    
    @EntityGraph(attributePaths = {"empleado"})
    List<registro_productividad> findByFechaBetween(LocalDate inicio, LocalDate fin);
    List<registro_productividad> findByAsistenciaId(Long asistenciaId);

        List<registro_productividad> findByAsistenciaIdAndEmpleadoId(
                Long asistenciaId,
                Long empleadoId
        );
                Optional<registro_productividad>
        findTopByEmpleadoIdAndFechaLessThanEqualOrderByFechaDesc(
                Long empleadoId,
                LocalDate fecha
        );
}
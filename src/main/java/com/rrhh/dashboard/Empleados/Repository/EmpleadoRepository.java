package com.rrhh.dashboard.Empleados.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rrhh.dashboard.Empleados.Entity.Empleados;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleados, Long> {

    List<Empleados> findBySector(String sector);
    
    Long countBySector(String sector);
    List<Empleados> findByPuesto(String puesto);

    Long countByPuesto(String puesto);

     Optional<Empleados> findByNombreAndApellido(String nombre, String apellido);

}
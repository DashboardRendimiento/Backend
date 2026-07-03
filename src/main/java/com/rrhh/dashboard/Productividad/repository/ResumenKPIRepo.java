package com.rrhh.dashboard.Productividad.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rrhh.dashboard.Migracion.Entity.ResumenKpi;

@Repository
public interface ResumenKPIRepo extends JpaRepository<ResumenKpi, Long> {}


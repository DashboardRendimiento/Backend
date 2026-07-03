package com.rrhh.dashboard.Productividad.Entity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rrhh.dashboard.Productividad.Entity.ProductividadDiaria;


@Repository
public interface ProductividadRepo extends JpaRepository<ProductividadDiaria, Long> {}


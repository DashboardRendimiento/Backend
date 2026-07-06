package com.rrhh.dashboard.Horarios.Repository;

import com.rrhh.dashboard.Horarios.Entity.WorkSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Long> {

    Optional<WorkSchedule> findByEmployeeId(Long employeeId);

    boolean existsByEmployeeId(Long employeeId);
}

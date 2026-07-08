package com.rrhh.dashboard.Asistencia.Repository;

import com.rrhh.dashboard.Asistencia.Entity.AttendanceRecord;
import com.rrhh.dashboard.Asistencia.Entity.EstadoVerificacionFacial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {

    Optional<AttendanceRecord> findFirstByEmployeeIdAndClockOutAtIsNullOrderByClockInAtDesc(Long employeeId);

    boolean existsByEmployeeIdAndClockOutAtIsNull(Long employeeId);

    List<AttendanceRecord> findAllByEmployeeId(Long employeeId);

    List<AttendanceRecord> findByEstadoVerificacion(EstadoVerificacionFacial estadoVerificacion);
}

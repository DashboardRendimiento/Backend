package com.rrhh.dashboard.Horarios.dtos;

import com.rrhh.dashboard.Horarios.Entity.WorkSchedule;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Set;

public record WorkScheduleResponse(
        Long id,
        Long employeeId,
        Set<DayOfWeek> workDays,
        LocalTime startTime,
        LocalTime endTime,
        Instant createdAt,
        Instant updatedAt
) {
    public static WorkScheduleResponse from(WorkSchedule schedule) {
        return new WorkScheduleResponse(
                schedule.getId(),
                schedule.getEmployeeId(),
                schedule.getWorkDays(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getCreatedAt(),
                schedule.getUpdatedAt());
    }
}

package com.rrhh.dashboard.Horarios.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

public record AssignWorkScheduleRequest(
        @NotNull Long employeeId,
        @NotEmpty Set<DayOfWeek> workDays,
        @NotNull LocalTime startTime,
        @NotNull LocalTime endTime
) {
}

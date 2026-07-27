package com.rrhh.dashboard.Objetivos.events;

import com.rrhh.dashboard.Objetivos.dtos.ObjetivoResponse;

public record ObjetivoAsignadoEvent(Long empleadoId, ObjetivoResponse objetivo) {
}

package com.rrhh.dashboard.Objetivos.exceptions;

import com.rrhh.dashboard.Objetivos.Entity.TipoObjetivo;

import java.time.LocalDate;

public class DuplicateObjetivoException extends RuntimeException {

    public DuplicateObjetivoException(Long empleadoId, TipoObjetivo tipo, LocalDate semanaInicio) {
        super("El empleado " + empleadoId + " ya tiene un objetivo de tipo " + tipo
                + " para la semana del " + semanaInicio);
    }
}

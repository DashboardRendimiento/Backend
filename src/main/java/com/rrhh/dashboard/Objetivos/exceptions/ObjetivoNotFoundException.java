package com.rrhh.dashboard.Objetivos.exceptions;

public class ObjetivoNotFoundException extends RuntimeException {

    public ObjetivoNotFoundException(Long id) {
        super("No existe un objetivo con id " + id);
    }
}

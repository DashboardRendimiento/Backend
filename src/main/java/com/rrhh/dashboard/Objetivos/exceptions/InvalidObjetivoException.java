package com.rrhh.dashboard.Objetivos.exceptions;

public class InvalidObjetivoException extends RuntimeException {

    public InvalidObjetivoException() {
        super("El valor semanal del objetivo debe ser mayor a cero");
    }
}

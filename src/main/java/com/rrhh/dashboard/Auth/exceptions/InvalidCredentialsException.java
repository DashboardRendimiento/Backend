package com.rrhh.dashboard.Auth.exceptions;

/**
 * Un unico tipo de excepcion para las cuatro causas de login invalido
 * (email inexistente, empleado inactivo, sin contrasena asignada todavia,
 * contrasena incorrecta): el controller responde siempre el mismo 401
 * generico, sin distinguir cual fallo, para no facilitar enumeracion de
 * usuarios.
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Credenciales invalidas");
    }
}

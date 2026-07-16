package com.rrhh.dashboard.Auth.services;

import com.rrhh.dashboard.Auth.exceptions.InvalidCredentialsException;
import com.rrhh.dashboard.Empleados.Entity.Empleados;
import com.rrhh.dashboard.Empleados.Repository.EmpleadoRepository;
import com.rrhh.dashboard.security.JwtIssuer;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AuthService {

    private final EmpleadoRepository empleadoRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtIssuer jwtIssuer;

    public AuthService(EmpleadoRepository empleadoRepository, PasswordEncoder passwordEncoder, JwtIssuer jwtIssuer) {
        this.empleadoRepository = empleadoRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtIssuer = jwtIssuer;
    }

    @Transactional(readOnly = true)
    public String login(String email, String rawPassword) {
        Empleados empleado = empleadoRepository.findByEmailIgnoreCase(email)
                .orElseThrow(InvalidCredentialsException::new);

      if (!empleado.isActive()) {
            log.warn("Intento de inicio de sesión con usuario inactivo. Email: {}", email);
            throw new InvalidCredentialsException();
        }

        if (empleado.getPasswordHash() == null) {
            log.error("El usuario {} no tiene contraseña almacenada en la base de datos.", email);
            throw new InvalidCredentialsException();
        }

        if (!passwordEncoder.matches(rawPassword, empleado.getPasswordHash())) {
            log.warn("Contraseña incorrecta para el usuario: {}", email);
            throw new InvalidCredentialsException();
        }
                return jwtIssuer.issue(empleado.getId(), empleado.getRole());

    }

    }


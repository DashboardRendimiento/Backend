package com.rrhh.dashboard.Auth.services;

import com.rrhh.dashboard.Auth.exceptions.InvalidCredentialsException;
import com.rrhh.dashboard.Empleados.Entity.Empleados;
import com.rrhh.dashboard.Empleados.Entity.EmployeeRole;
import com.rrhh.dashboard.Empleados.Repository.EmpleadoRepository;
import com.rrhh.dashboard.security.JwtIssuer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtIssuer jwtIssuer;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(empleadoRepository, passwordEncoder, jwtIssuer);
    }

    private Empleados activeEmployeeWithPassword(String hash) {
        Empleados empleado = new Empleados();
        empleado.setNombre("Ana");
        empleado.setApellido("Perez");
        empleado.setEmail("ana.perez@example.com");
        empleado.setPuesto("Analista");
        empleado.setRole(EmployeeRole.EMPLEADO);
        empleado.setPasswordHash(hash);
        return empleado;
    }

    @Test
    void validCredentialsIssueToken() {
        Empleados empleado = activeEmployeeWithPassword("bcrypt-hash");
        when(empleadoRepository.findByEmailIgnoreCase("ana.perez@example.com"))
                .thenReturn(Optional.of(empleado));
        when(passwordEncoder.matches("Sup3rSecret!", "bcrypt-hash")).thenReturn(true);
        when(jwtIssuer.issue(any(), eq(EmployeeRole.EMPLEADO))).thenReturn("signed-jwt");

        String token = authService.login("ana.perez@example.com", "Sup3rSecret!");

        assertThat(token).isEqualTo("signed-jwt");
    }

    @Test
    void wrongPasswordThrowsInvalidCredentials() {
        Empleados empleado = activeEmployeeWithPassword("bcrypt-hash");
        when(empleadoRepository.findByEmailIgnoreCase("ana.perez@example.com"))
                .thenReturn(Optional.of(empleado));
        when(passwordEncoder.matches("wrong", "bcrypt-hash")).thenReturn(false);

        assertThatThrownBy(() -> authService.login("ana.perez@example.com", "wrong"))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void unknownEmailThrowsInvalidCredentials() {
        when(empleadoRepository.findByEmailIgnoreCase("nadie@example.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login("nadie@example.com", "whatever"))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void inactiveEmployeeThrowsInvalidCredentialsEvenWithCorrectPassword() {
        Empleados empleado = activeEmployeeWithPassword("bcrypt-hash");
        empleado.setActive(false);
        when(empleadoRepository.findByEmailIgnoreCase("ana.perez@example.com"))
                .thenReturn(Optional.of(empleado));

        assertThatThrownBy(() -> authService.login("ana.perez@example.com", "Sup3rSecret!"))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void employeeWithoutPasswordAssignedThrowsInvalidCredentials() {
        Empleados empleado = new Empleados();
        empleado.setNombre("Ana");
        empleado.setApellido("Perez");
        empleado.setEmail("ana.perez@example.com");
        empleado.setRole(EmployeeRole.EMPLEADO);
        // sin setPasswordHash(): empleado sin contrasena asignada todavia.
        when(empleadoRepository.findByEmailIgnoreCase("ana.perez@example.com"))
                .thenReturn(Optional.of(empleado));

        assertThatThrownBy(() -> authService.login("ana.perez@example.com", "cualquier-cosa"))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}

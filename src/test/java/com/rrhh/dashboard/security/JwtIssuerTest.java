package com.rrhh.dashboard.security;

import com.rrhh.dashboard.Empleados.Entity.EmployeeRole;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Round-trip emision (JwtIssuer) / validacion (JwtRoleReader) con el mismo
 * secreto: confirma que el JWT emitido por el login trae sub=employeeId y
 * rol, leibles por el filtro que ya existia.
 */
class JwtIssuerTest {

    private static final String SECRET = "test-secret-0123456789abcdef0123456789abcdef";

    @Test
    void issuedTokenCarriesSubjectAndRoleReadableByJwtRoleReader() {
        JwtIssuer jwtIssuer = new JwtIssuer(SECRET, 60);
        JwtRoleReader jwtRoleReader = new JwtRoleReader(SECRET);
        Long employeeId = 42L;

        String token = jwtIssuer.issue(employeeId, EmployeeRole.ADMINISTRADOR);

        assertThat(jwtRoleReader.readRole(token)).contains("ADMINISTRADOR");
        assertThat(jwtRoleReader.readEmployeeId(token)).contains(employeeId);
    }

    @Test
    void tokenSignedWithDifferentSecretIsNotReadable() {
        JwtIssuer jwtIssuer = new JwtIssuer(SECRET, 60);
        JwtRoleReader otherReader = new JwtRoleReader("another-secret-0123456789abcdef0123456789");
        Long employeeId = 42L;

        String token = jwtIssuer.issue(employeeId, EmployeeRole.EMPLEADO);

        assertThat(otherReader.readRole(token)).isEmpty();
        assertThat(otherReader.readEmployeeId(token)).isEmpty();
    }
}

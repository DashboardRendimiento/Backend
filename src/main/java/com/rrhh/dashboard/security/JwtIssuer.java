package com.rrhh.dashboard.security;

import com.rrhh.dashboard.Empleados.Entity.EmployeeRole;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtIssuer {

    private final SecretKey signingKey;
    private final Duration expiration;

    public JwtIssuer(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration-minutes:60}") long expirationMinutes) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = Duration.ofMinutes(expirationMinutes);
    }

    public String issue(Long employeeId, EmployeeRole role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(employeeId.toString())
                .claim("rol", role.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expiration)))
                .signWith(signingKey)
                .compact();
    }
}

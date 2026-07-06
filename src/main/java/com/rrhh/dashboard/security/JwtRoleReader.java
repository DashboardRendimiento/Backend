package com.rrhh.dashboard.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
public class JwtRoleReader {

    static final String ROLE_CLAIM = "rol";

    private final SecretKey signingKey;

    public JwtRoleReader(@Value("${app.jwt.secret}") String secret) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Optional<String> readRole(String token) {
        try {
            Claims claims = parseClaims(token);
            return Optional.ofNullable(claims.get(ROLE_CLAIM, String.class));
        } catch (JwtException | IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    public Optional<Long> readEmployeeId(String token) {
        try {
            Claims claims = parseClaims(token);
            String subject = claims.getSubject();
            return subject == null ? Optional.empty() : Optional.of(Long.valueOf(subject));
        } catch (JwtException | IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

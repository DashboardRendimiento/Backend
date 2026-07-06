package com.rrhh.dashboard.security;

import com.rrhh.dashboard.Empleados.Entity.EmployeeRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Fallback de query param (`?token=`) acotado a {@link #SSE_STREAM_PATH}: el
 * {@code EventSource} nativo del navegador no permite adjuntar el header
 * {@code Authorization}, necesario para un futuro stream SSE del dashboard.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String SSE_STREAM_PATH = "/api/dashboard/stream";
    private static final String TOKEN_QUERY_PARAM = "token";

    private final JwtRoleReader jwtRoleReader;

    public JwtAuthenticationFilter(JwtRoleReader jwtRoleReader) {
        this.jwtRoleReader = jwtRoleReader;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        extractToken(request).ifPresent(token -> {
            Optional<EmployeeRole> role = jwtRoleReader.readRole(token).flatMap(this::toEmployeeRole);
            Optional<Long> employeeId = jwtRoleReader.readEmployeeId(token);

            if (role.isPresent() && employeeId.isPresent()) {
                authenticate(new AuthenticatedEmployee(employeeId.get(), role.get()));
            }
        });

        filterChain.doFilter(request, response);
    }

    private Optional<String> extractToken(HttpServletRequest request) {
        String header = request.getHeader(AUTH_HEADER);
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return Optional.of(header.substring(BEARER_PREFIX.length()));
        }
        if (SSE_STREAM_PATH.equals(request.getRequestURI())) {
            String queryToken = request.getParameter(TOKEN_QUERY_PARAM);
            if (queryToken != null && !queryToken.isBlank()) {
                return Optional.of(queryToken);
            }
        }
        return Optional.empty();
    }

    private Optional<EmployeeRole> toEmployeeRole(String rawRole) {
        try {
            return Optional.of(EmployeeRole.valueOf(rawRole));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    private void authenticate(AuthenticatedEmployee principal) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal, null,
                List.of(new SimpleGrantedAuthority("ROLE_" + principal.role().name())));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}

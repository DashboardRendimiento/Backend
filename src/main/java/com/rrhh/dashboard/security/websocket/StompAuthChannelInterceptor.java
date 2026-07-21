package com.rrhh.dashboard.security.websocket;

import com.rrhh.dashboard.Empleados.Entity.EmployeeRole;
import com.rrhh.dashboard.security.AuthenticatedEmployee;
import com.rrhh.dashboard.security.JwtRoleReader;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Autentica el frame STOMP CONNECT con el mismo JWT que el resto de la API
 * (header {@code Authorization: Bearer <token>}, pero como header STOMP, no
 * HTTP — el handshake de SockJS/WebSocket no lo lleva). Ademas, en cada
 * SUBSCRIBE a {@code /topic/productividad/{empleadoId}} valida que quien se
 * suscribe sea ese empleado o tenga un rol de supervision; si no, corta la
 * suscripcion en vez de dejar que cualquiera escuche el KPI de otro.
 */
@Component
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private static final String PRODUCTIVIDAD_TOPIC_PREFIX = "/topic/productividad/";
    private static final List<EmployeeRole> ROLES_SUPERVISION =
            List.of(EmployeeRole.ADMINISTRADOR, EmployeeRole.SUPERVISOR, EmployeeRole.SUPERADMIN);

    private final JwtRoleReader jwtRoleReader;
    public StompAuthChannelInterceptor(JwtRoleReader jwtRoleReader) {
        this.jwtRoleReader = jwtRoleReader;
    }


    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || accessor.getCommand() == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            accessor.setUser(authenticate(accessor));
        } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            authorizeSubscription(accessor);
        }

        return message;
    }

    private Authentication authenticate(StompHeaderAccessor accessor) {
        String token = extractToken(accessor);
        if (token == null) {
            throw new AuthenticationCredentialsNotFoundException("Falta el token JWT en el CONNECT");
        }

        Optional<EmployeeRole> role = jwtRoleReader.readRole(token).flatMap(this::toEmployeeRole);
        Optional<Long> employeeId = jwtRoleReader.readEmployeeId(token);
        if (role.isEmpty() || employeeId.isEmpty()) {
            throw new BadCredentialsException("Token JWT invalido o expirado");
        }

        AuthenticatedEmployee principal = new AuthenticatedEmployee(employeeId.get(), role.get());
        return new UsernamePasswordAuthenticationToken(
                principal, null, List.of(new SimpleGrantedAuthority("ROLE_" + role.get().name())));
    }

    private void authorizeSubscription(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        if (destination == null || !destination.startsWith(PRODUCTIVIDAD_TOPIC_PREFIX)) {
            return;
        }

        if (!(accessor.getUser() instanceof Authentication authentication)
                || !(authentication.getPrincipal() instanceof AuthenticatedEmployee principal)) {
            throw new AuthenticationCredentialsNotFoundException("No autenticado");
        }

        if (ROLES_SUPERVISION.contains(principal.role())) {
            return;
        }

        String idSolicitado = destination.substring(PRODUCTIVIDAD_TOPIC_PREFIX.length());
        if (!principal.employeeId().toString().equals(idSolicitado)) {
            throw new AccessDeniedException("No puede suscribirse a la productividad de otro empleado");
        }
    }

    private String extractToken(StompHeaderAccessor accessor) {
        String header = accessor.getFirstNativeHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring("Bearer ".length());
        }
        return null;
    }

    private Optional<EmployeeRole> toEmployeeRole(String rawRole) {
        try {
            return Optional.of(EmployeeRole.valueOf(rawRole));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
}

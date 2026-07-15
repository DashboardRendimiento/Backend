package com.rrhh.dashboard.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * El resto de la API (Empleados/Productividad/Migracion) sigue abierta sin
 * token, igual que antes de este cambio: la proteccion real de los
 * endpoints nuevos (Asistencia/Horarios) es via {@code @PreAuthorize} a
 * nivel de metodo, no a nivel de URL.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtRoleReader jwtRoleReader;

    public SecurityConfig(JwtRoleReader jwtRoleReader) {
        this.jwtRoleReader = jwtRoleReader;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(org.springframework.security.config.Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .addFilterBefore(new JwtAuthenticationFilter(jwtRoleReader),
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}

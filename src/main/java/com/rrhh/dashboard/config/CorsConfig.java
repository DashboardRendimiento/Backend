package com.rrhh.dashboard.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuracion de CORS (Cross-Origin Resource Sharing)
 * Permite que el frontend consuma la API desde diferentes origenes
 * 
 * @author Backend Dev 1
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                // Origenes permitidos (frontend)
                .allowedOrigins(
                    "http://localhost:3000",      // React/Vue dev server
                    "http://localhost:8080",      // Frontend estatico
                    "http://localhost:5173",      // Vite dev server
                    "http://localhost:4200",      // Angular dev server
                    "http://127.0.0.1:3000",
                    "http://127.0.0.1:8080",
                    "http://127.0.0.1:4200",
                    "https://hoppscotch.io"       // Hoppscotch (app web)
                )
                // Metodos HTTP permitidos
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                // Headers permitidos
                .allowedHeaders("*")
                // Permite credenciales (cookies, authorization headers)
                .allowCredentials(true)
                // Tiempo de cache de la configuracion CORS
                .maxAge(3600);
        
        System.out.println("✅ CORS configurado correctamente");
    }
}

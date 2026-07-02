package com.rrhh.dashboard.controller;

import com.rrhh.dashboard.service.ExcelDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller para verificar el estado de salud de la aplicacion
 * 
 * @author Backend Dev 1
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HealthController {
    
    private final ExcelDataService excelDataService;
    
    /**
     * Endpoint basico de health check
     * GET /api/health
     * 
     * @return Estado del servicio
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("service", "Dashboard RRHH API");
        response.put("version", "1.0.0");
        response.put("timestamp", LocalDateTime.now());
        response.put("dataLoaded", excelDataService.isDataLoaded());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint para obtener informacion detallada del sistema
     * GET /api/info
     * 
     * @return Informacion detallada del sistema
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> response = new HashMap<>();
        
        // Informacion basica
        response.put("application", "Dashboard RRHH");
        response.put("version", "1.0.0");
        response.put("description", "API REST para gestion de recursos humanos de deposito");
        response.put("timestamp", LocalDateTime.now());
        
        // Estado de los datos
        response.put("dataLoaded", excelDataService.isDataLoaded());
        response.put("availableSheets", excelDataService.getAvailableSheets());
        response.put("dataStatistics", excelDataService.getDataStatistics());
        
        // Informacion del sistema
        Map<String, Object> system = new HashMap<>();
        system.put("javaVersion", System.getProperty("java.version"));
        system.put("osName", System.getProperty("os.name"));
        system.put("osVersion", System.getProperty("os.version"));
        response.put("system", system);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint raiz de la API
     * GET /api
     * 
     * @return Mensaje de bienvenida
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> root() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Dashboard RRHH API");
        response.put("version", "1.0.0");
        response.put("status", "running");
        response.put("endpoints", Map.of(
            "health", "/api/health",
            "info", "/api/info",
            "kpis", "/api/v1/kpis",
            "empleados", "/api/v1/empleados"
        ));
        
        return ResponseEntity.ok(response);
    }
}

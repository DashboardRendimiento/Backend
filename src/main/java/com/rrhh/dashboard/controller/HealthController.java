package com.rrhh.dashboard.controller;

import com.rrhh.dashboard.service.ExcelDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    private final ExcelDataService excelDataService;
    public HealthController(ExcelDataService excelDataService) {
        this.excelDataService = excelDataService;
    }


    /**
     * Health check básico del sistema
     * GET /api/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
                Map<String, Object> resp = new HashMap<>();
                resp.put("status", "healthy");
                resp.put("service", "Dashboard RRHH API");
                resp.put("version", "1.0.0");
                resp.put("timestamp", LocalDateTime.now());
                resp.put("dataLoaded", excelDataService.isDataLoaded());
                resp.put("empleadosCount", excelDataService.getEmpleados().size());
                resp.put("resumenKpisCount", excelDataService.getResumen().size());
                return ResponseEntity.ok(resp);
    }

    /**
     * Información general del sistema y datos cargados
     * GET /api/info
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("application", "Dashboard RRHH");
        resp.put("version", "1.0.0");
        resp.put("description", "API REST para gestión de RRHH de depósito");
        resp.put("timestamp", LocalDateTime.now());

        // Estado de datos (desde servicio real)
        resp.put("dataLoaded", excelDataService.isDataLoaded());
        resp.put("empleados", excelDataService.getEmpleados().size());
        resp.put("productividad", excelDataService.getProductividad().size());
        resp.put("asistencia", excelDataService.getAsistencia().size());
        resp.put("resumenKpis", excelDataService.getResumen().size());

        // Información del sistema
        Map<String, String> system = new HashMap<>();
        system.put("javaVersion", System.getProperty("java.version"));
        system.put("osName", System.getProperty("os.name"));
        system.put("osVersion", System.getProperty("os.version"));
        resp.put("system", system);

        return ResponseEntity.ok(resp);
    }

    /**
     * Endpoint raíz de la API
     * GET /api
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> root() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("message", "Dashboard RRHH API");
        resp.put("version", "1.0.0");
        resp.put("status", "running");
        resp.put("timestamp", LocalDateTime.now());
        resp.put("dataLoaded", excelDataService.isDataLoaded());
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("health", "/api/health");
        endpoints.put("info", "/api/info");
        endpoints.put("kpis", "/api/v1/kpis");
        endpoints.put("empleados", "/api/v1/empleados");
        resp.put("endpoints", endpoints);
        return ResponseEntity.ok(resp);
    }
}
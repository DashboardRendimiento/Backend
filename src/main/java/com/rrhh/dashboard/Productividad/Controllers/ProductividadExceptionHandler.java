package com.rrhh.dashboard.Productividad.Controllers;

import com.rrhh.dashboard.Productividad.exceptions.YaRegistradoEnEstaHoraException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ProductividadExceptionHandler {

    @ExceptionHandler(YaRegistradoEnEstaHoraException.class)
    public ResponseEntity<Map<String, Object>> handleYaRegistrado(YaRegistradoEnEstaHoraException ex) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("timestamp", Instant.now().toString());
        payload.put("status", HttpStatus.CONFLICT.value());
        payload.put("error", HttpStatus.CONFLICT.getReasonPhrase());
        payload.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(payload);
    }
}

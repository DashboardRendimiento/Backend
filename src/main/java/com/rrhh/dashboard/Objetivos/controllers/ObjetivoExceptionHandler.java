package com.rrhh.dashboard.Objetivos.controllers;

import com.rrhh.dashboard.Objetivos.exceptions.DuplicateObjetivoException;
import com.rrhh.dashboard.Objetivos.exceptions.ForbiddenObjetivoAccessException;
import com.rrhh.dashboard.Objetivos.exceptions.InvalidObjetivoException;
import com.rrhh.dashboard.Objetivos.exceptions.ObjetivoNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ObjetivoExceptionHandler {

    @ExceptionHandler(ObjetivoNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ObjetivoNotFoundException ex) {
        return body(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DuplicateObjetivoException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicate(DuplicateObjetivoException ex) {
        return body(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(InvalidObjetivoException.class)
    public ResponseEntity<Map<String, Object>> handleInvalid(InvalidObjetivoException ex) {
        return body(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ForbiddenObjetivoAccessException.class)
    public ResponseEntity<Map<String, Object>> handleForbidden(ForbiddenObjetivoAccessException ex) {
        return body(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> body(HttpStatus status, String message) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("timestamp", Instant.now().toString());
        payload.put("status", status.value());
        payload.put("error", status.getReasonPhrase());
        payload.put("message", message);
        return ResponseEntity.status(status).body(payload);
    }
}

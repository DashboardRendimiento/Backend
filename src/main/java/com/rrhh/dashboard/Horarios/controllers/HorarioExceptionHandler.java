package com.rrhh.dashboard.Horarios.controllers;

import com.rrhh.dashboard.Horarios.exceptions.DuplicateScheduleException;
import com.rrhh.dashboard.Horarios.exceptions.EmptyWorkDaysException;
import com.rrhh.dashboard.Horarios.exceptions.InvalidScheduleTimeRangeException;
import com.rrhh.dashboard.Horarios.exceptions.WorkScheduleNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class HorarioExceptionHandler {

    @ExceptionHandler(WorkScheduleNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(WorkScheduleNotFoundException ex) {
        return body(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DuplicateScheduleException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicate(DuplicateScheduleException ex) {
        return body(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(EmptyWorkDaysException.class)
    public ResponseEntity<Map<String, Object>> handleEmptyWorkDays(EmptyWorkDaysException ex) {
        return body(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InvalidScheduleTimeRangeException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidRange(InvalidScheduleTimeRangeException ex) {
        return body(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Solicitud invalida");
        return body(HttpStatus.BAD_REQUEST, message);
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

package com.rrhh.dashboard.Asistencia.controllers;

import com.rrhh.dashboard.Asistencia.exceptions.AlreadyClockedInException;
import com.rrhh.dashboard.Asistencia.exceptions.AlreadyClockedOutException;
import com.rrhh.dashboard.Asistencia.exceptions.AttendanceRecordNotFoundException;
import com.rrhh.dashboard.Asistencia.exceptions.ForbiddenAttendanceAccessException;
import com.rrhh.dashboard.Asistencia.exceptions.NoOpenAttendanceRecordException;
import com.rrhh.dashboard.Asistencia.exceptions.RevisionNoAplicableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class AsistenciaExceptionHandler {

    @ExceptionHandler(AlreadyClockedInException.class)
    public ResponseEntity<Map<String, Object>> handleAlreadyClockedIn(AlreadyClockedInException ex) {
        return body(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(NoOpenAttendanceRecordException.class)
    public ResponseEntity<Map<String, Object>> handleNoOpenRecord(NoOpenAttendanceRecordException ex) {
        return body(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(AlreadyClockedOutException.class)
    public ResponseEntity<Map<String, Object>> handleAlreadyClockedOut(AlreadyClockedOutException ex) {
        return body(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(ForbiddenAttendanceAccessException.class)
    public ResponseEntity<Map<String, Object>> handleForbiddenAccess(ForbiddenAttendanceAccessException ex) {
        return body(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(AttendanceRecordNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(AttendanceRecordNotFoundException ex) {
        return body(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(RevisionNoAplicableException.class)
    public ResponseEntity<Map<String, Object>> handleRevisionNoAplicable(RevisionNoAplicableException ex) {
        return body(HttpStatus.CONFLICT, ex.getMessage());
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

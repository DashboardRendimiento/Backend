package com.rrhh.dashboard.Asistencia.controllers;

import com.rrhh.dashboard.Asistencia.Entity.AttendanceRecord;
import com.rrhh.dashboard.Asistencia.dtos.AttendanceRecordResponse;
import com.rrhh.dashboard.Asistencia.dtos.RevisarFichajeRequest;
import com.rrhh.dashboard.Asistencia.services.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    /**
     * multipart/form-data: la parte "foto" es la captura para verificacion
     * facial. El fichaje se registra igual este presente o no, y aunque la
     * verificacion de baja resultado insuficiente (ver AttendanceService).
     */
    @PostMapping(value = "/{employeeId}/clock-in", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('EMPLEADO', 'ADMINISTRADOR', 'SUPERADMIN', 'SUPERVISOR')")
    public ResponseEntity<AttendanceRecordResponse> clockIn(@PathVariable Long employeeId,
                                                             @RequestPart(value = "foto", required = false) MultipartFile foto) {
        AttendanceRecord record = attendanceService.clockIn(employeeId, authenticatedEmployeeId(), leerBytes(foto));
        return ResponseEntity.ok(AttendanceRecordResponse.from(record));
    }

    @PostMapping("/{employeeId}/clock-out")
    @PreAuthorize("hasAnyRole('EMPLEADO', 'ADMINISTRADOR', 'SUPERADMIN', 'SUPERVISOR')")
    public ResponseEntity<AttendanceRecordResponse> clockOut(@PathVariable Long employeeId) {
        AttendanceRecord record = attendanceService.clockOut(employeeId, authenticatedEmployeeId());
        return ResponseEntity.ok(AttendanceRecordResponse.from(record));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPERADMIN', 'SUPERVISOR')")
    public ResponseEntity<List<AttendanceRecordResponse>> listarTodos() {
        List<AttendanceRecordResponse> response = attendanceService.listAll().stream()
                .map(AttendanceRecordResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/empleado/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPERADMIN', 'SUPERVISOR')")
    public ResponseEntity<List<AttendanceRecordResponse>> listarPorEmpleado(@PathVariable Long employeeId) {
        List<AttendanceRecordResponse> response = attendanceService.listByEmployee(employeeId).stream()
                .map(AttendanceRecordResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * Fichajes de Entrada pendientes de revision humana (verificacion
     * facial automatica insuficiente, sin foto de referencia, o el
     * servicio de reconocimiento no respondio).
     */
    @GetMapping("/pendientes")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPERADMIN', 'SUPERVISOR')")
    public ResponseEntity<List<AttendanceRecordResponse>> listarPendientes() {
        List<AttendanceRecordResponse> response = attendanceService.listarPendientesDeRevision().stream()
                .map(AttendanceRecordResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * Foto capturada en ese fichaje, para que quien revisa la compare a
     * simple vista contra la foto de referencia del empleado
     * (GET /api/empleados/{id}/foto).
     */
    @GetMapping("/{id}/foto")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPERADMIN', 'SUPERVISOR')")
    public ResponseEntity<byte[]> obtenerFoto(@PathVariable Long id) {
        byte[] foto = attendanceService.obtenerFotoFichaje(id);
        if (foto == null || foto.length == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(foto);
    }

    @PostMapping("/{id}/revisar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPERADMIN', 'SUPERVISOR')")
    public ResponseEntity<AttendanceRecordResponse> revisar(@PathVariable Long id,
                                                             @Valid @RequestBody RevisarFichajeRequest request) {
        AttendanceRecord record = attendanceService.revisar(id, request.aprobado());
        return ResponseEntity.ok(AttendanceRecordResponse.from(record));
    }

    /**
     * Deriva el employeeId del empleado autenticado a partir de
     * Authentication.getName() (AuthenticatedEmployee.getName()), sin
     * depender del tipo concreto (vive en com.rrhh.dashboard.security).
     */
    private Long authenticatedEmployeeId() {
        return Long.valueOf(
                SecurityContextHolder.getContext().getAuthentication().getName());
    }

    private byte[] leerBytes(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            return null;
        }
        try {
            return archivo.getBytes();
        } catch (IOException e) {
            throw new UncheckedIOException("Error leyendo el archivo subido", e);
        }
    }
}

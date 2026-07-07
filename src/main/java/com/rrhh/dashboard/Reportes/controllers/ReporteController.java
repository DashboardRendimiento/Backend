package com.rrhh.dashboard.Reportes.controllers;

import com.rrhh.dashboard.Reportes.services.ReporteService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Informes de Productividad/Asistencia para el rol SUPERVISOR (y
 * Administrador/SuperAdmin), en Excel o CSV segun el parametro "formato".
 */
@RestController
@RequestMapping("/api/reportes")
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPERADMIN', 'SUPERVISOR')")
public class ReporteController {

    private static final MediaType EXCEL = MediaType.parseMediaType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/productividad")
    public ResponseEntity<byte[]> productividad(@RequestParam(defaultValue = "excel") String formato) {
        boolean excel = "excel".equalsIgnoreCase(formato);
        byte[] contenido = excel ? reporteService.productividadExcel() : reporteService.productividadCsv();
        return archivo(contenido, excel, "productividad");
    }

    @GetMapping("/asistencia")
    public ResponseEntity<byte[]> asistencia(@RequestParam(defaultValue = "excel") String formato) {
        boolean excel = "excel".equalsIgnoreCase(formato);
        byte[] contenido = excel ? reporteService.asistenciaExcel() : reporteService.asistenciaCsv();
        return archivo(contenido, excel, "asistencia");
    }

    private ResponseEntity<byte[]> archivo(byte[] contenido, boolean excel, String nombre) {
        String extension = excel ? ".xlsx" : ".csv";
        MediaType mediaType = excel ? EXCEL : MediaType.parseMediaType("text/csv");
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(nombre + extension).build().toString())
                .body(contenido);
    }
}

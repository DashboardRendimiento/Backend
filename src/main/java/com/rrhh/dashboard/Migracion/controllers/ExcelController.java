package com.rrhh.dashboard.Migracion.controllers;

import com.rrhh.dashboard.Migracion.Dtos.ExcelRowDTO;
import com.rrhh.dashboard.Migracion.service.ExcelImportService;
import com.rrhh.dashboard.Migracion.service.ExcelProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/excel")
public class ExcelController {

    private final ExcelImportService excelImportService;
    private final ExcelProcessingService excelProcessingService;
    public ExcelController(ExcelImportService excelImportService, ExcelProcessingService excelProcessingService) {
        this.excelImportService = excelImportService;
        this.excelProcessingService = excelProcessingService;
    }


    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> importExcel(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validar que el archivo no esté vacío
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "El archivo está vacío");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Validar que sea un archivo Excel
            String fileName = file.getOriginalFilename();
            if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
                response.put("success", false);
                response.put("message", "El archivo debe ser un Excel (.xlsx o .xls)");
                return ResponseEntity.badRequest().body(response);
            }
            
            System.out.println("Procesando archivo: " + fileName);
            System.out.println("Tamaño: " + file.getSize() + " bytes");
            
            // 1. Leer el Excel
            List<ExcelRowDTO> excelRows = excelImportService.readExcelFile(file);
            
            // 2. Procesar y guardar datos
            excelProcessingService.processExcelData(excelRows);
            
            response.put("success", true);
            response.put("message", "Excel importado exitosamente");
            response.put("rowsProcessed", excelRows.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Error al importar Excel: " + e.getMessage());
            e.printStackTrace();
            
            response.put("success", false);
            response.put("message", "Error al importar Excel: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
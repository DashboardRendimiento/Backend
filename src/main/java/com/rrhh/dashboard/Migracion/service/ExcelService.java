package com.rrhh.dashboard.Migracion.service;


import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Iterator;

@Service
@Slf4j
public class ExcelService {
    
    public Workbook obtenerWorkbook(MultipartFile archivo) throws IOException {
        return new XSSFWorkbook(archivo.getInputStream());
    }
    
    public Sheet obtenerHoja(Workbook workbook, String nombreHoja) {
        Sheet sheet = workbook.getSheet(nombreHoja);
        if (sheet == null) {
            log.warn("Hoja '{}' no encontrada", nombreHoja);
        }
        return sheet;
    }
    
    public Iterator<Row> obtenerFilas(Sheet sheet) {
        return sheet.iterator();
    }
    
    public void saltarCabecera(Iterator<Row> rows) {
        if (rows.hasNext()) {
            rows.next();
        }
    }
    
    // ================ MÉTODOS DE EXTRACCIÓN DE VALORES ================
    
    public String getStringValue(Cell cell) {
        if (cell == null) return null;
        try {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue().trim();
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getDateCellValue().toString();
                    }
                    return String.valueOf((long) cell.getNumericCellValue());
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                case FORMULA:
                    return cell.getCellFormula();
                default:
                    return null;
            }
        } catch (Exception e) {
            log.error("Error al obtener valor String de celda", e);
            return null;
        }
    }
    
    public Integer getIntegerValue(Cell cell) {
        if (cell == null) return null;
        try {
            switch (cell.getCellType()) {
                case NUMERIC:
                    return (int) cell.getNumericCellValue();
                case STRING:
                    try {
                        return Integer.parseInt(cell.getStringCellValue().trim());
                    } catch (NumberFormatException e) {
                        return null;
                    }
                default:
                    return null;
            }
        } catch (Exception e) {
            log.error("Error al obtener valor Integer de celda", e);
            return null;
        }
    }
    
    public Double getDoubleValue(Cell cell) {
        if (cell == null) return null;
        try {
            switch (cell.getCellType()) {
                case NUMERIC:
                    return cell.getNumericCellValue();
                case STRING:
                    try {
                        return Double.parseDouble(cell.getStringCellValue().trim());
                    } catch (NumberFormatException e) {
                        return null;
                    }
                default:
                    return null;
            }
        } catch (Exception e) {
            log.error("Error al obtener valor Double de celda", e);
            return null;
        }
    }
    
    public LocalDate getDateValue(Cell cell) {
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            }
            return null;
        } catch (Exception e) {
            log.error("Error al obtener fecha de celda", e);
            return null;
        }
    }
}
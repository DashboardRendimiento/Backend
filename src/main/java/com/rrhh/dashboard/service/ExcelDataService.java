package com.rrhh.dashboard.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio para cargar y gestionar datos desde archivo Excel
 * Carga los datos en memoria al iniciar la aplicacion usando @PostConstruct
 * 
 * @author Backend Dev 1
 */
@Service
@Slf4j
public class ExcelDataService {
    
    @Value("${app.excel.file}")
    private Resource excelFile;
    
    // Almacenamiento en memoria de todas las hojas del Excel
    private final Map<String, List<Map<String, Object>>> data = new HashMap<>();
    
    // Nombres de las hojas a cargar
    private static final String[] SHEET_NAMES = {
        "Resumen_KPIs", 
        "Empleados", 
        "Productividad_Diaria",
        "Asistencia_Diaria", 
        "Seguridad", 
        "Capacitaciones"
    };
    
    /**
     * Carga los datos del Excel al iniciar la aplicacion
     * Se ejecuta automaticamente despues de la construccion del bean
     */
    @PostConstruct
    public void loadExcelData() {
        log.info("═══════════════════════════════════════════════════════");
        log.info("🔄 Iniciando carga de datos desde Excel...");
        log.info("═══════════════════════════════════════════════════════");
        
        try {
            InputStream inputStream = excelFile.getInputStream();
            Workbook workbook = new XSSFWorkbook(inputStream);
            
            for (String sheetName : SHEET_NAMES) {
                Sheet sheet = workbook.getSheet(sheetName);
                if (sheet != null) {
                    List<Map<String, Object>> sheetData = parseSheet(sheet);
                    data.put(sheetName, sheetData);
                    log.info("✅ Cargada hoja: {} ({} registros)", sheetName, sheetData.size());
                } else {
                    log.warn("⚠️  Hoja no encontrada: {}", sheetName);
                }
            }
            
            workbook.close();
            inputStream.close();
            
            log.info("═══════════════════════════════════════════════════════");
            log.info("✨ Datos cargados exitosamente");
            log.info("📊 Total de hojas cargadas: {}", data.size());
            log.info("═══════════════════════════════════════════════════════");
            
        } catch (Exception e) {
            log.error("❌ Error cargando archivo Excel: {}", e.getMessage(), e);
            throw new RuntimeException("No se pudo cargar el archivo Excel: " + e.getMessage(), e);
        }
    }
    
    /**
     * Parsea una hoja de Excel y la convierte en una lista de mapas
     * Cada mapa representa una fila con clave=nombreColumna, valor=valorCelda
     */
    private List<Map<String, Object>> parseSheet(Sheet sheet) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        // Obtener fila de headers (primera fila)
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            log.warn("La hoja {} no tiene headers", sheet.getSheetName());
            return result;
        }
        
        // Extraer nombres de columnas
        List<String> headers = new ArrayList<>();
        for (Cell cell : headerRow) {
            String headerName = getCellValueAsString(cell);
            headers.add(headerName);
        }
        
        // Procesar filas de datos (desde la fila 1 en adelante)
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            
            Map<String, Object> rowData = new HashMap<>();
            boolean hasData = false;
            
            for (int j = 0; j < headers.size(); j++) {
                Cell cell = row.getCell(j);
                Object value = getCellValue(cell);
                rowData.put(headers.get(j), value);
                
                if (value != null && !value.toString().trim().isEmpty()) {
                    hasData = true;
                }
            }
            
            // Solo agregar filas que tengan al menos un dato
            if (hasData) {
                result.add(rowData);
            }
        }
        
        return result;
    }
    
    /**
     * Obtiene el valor de una celda segun su tipo
     */
    private Object getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    // Convertir fecha de Excel a LocalDateTime
                    Date date = cell.getDateCellValue();
                    yield date.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();
                } else {
                    yield cell.getNumericCellValue();
                }
            }
            case BOOLEAN -> cell.getBooleanCellValue();
            case FORMULA -> {
                try {
                    yield cell.getNumericCellValue();
                } catch (Exception e) {
                    yield cell.getStringCellValue();
                }
            }
            case BLANK -> "";
            default -> "";
        };
    }
    
    /**
     * Obtiene el valor de una celda como String (para headers)
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }
    
    /**
     * Obtiene todos los datos de una hoja especifica
     * 
     * @param sheetName Nombre de la hoja
     * @return Lista de registros (cada registro es un Map)
     */
    public List<Map<String, Object>> getData(String sheetName) {
        List<Map<String, Object>> sheetData = data.get(sheetName);
        if (sheetData == null) {
            log.warn("⚠️  Hoja no encontrada: {}", sheetName);
            return new ArrayList<>();
        }
        return new ArrayList<>(sheetData);
    }
    
    /**
     * Busca un registro por ID en una hoja especifica
     * 
     * @param sheetName Nombre de la hoja
     * @param id Valor del ID a buscar (campo ID_Empleado)
     * @return Optional con el registro encontrado
     */
    public Optional<Map<String, Object>> findById(String sheetName, String id) {
        return getData(sheetName).stream()
                .filter(row -> id.equals(row.get("ID_Empleado")))
                .findFirst();
    }
    
    /**
     * Filtra registros por un campo especifico
     * 
     * @param sheetName Nombre de la hoja
     * @param field Nombre del campo a filtrar
     * @param value Valor a buscar
     * @return Lista de registros que cumplen el filtro
     */
    public List<Map<String, Object>> filterByField(String sheetName, String field, Object value) {
        return getData(sheetName).stream()
                .filter(row -> value.equals(row.get(field)))
                .collect(Collectors.toList());
    }
    
    /**
     * Filtra registros por multiples campos
     * 
     * @param sheetName Nombre de la hoja
     * @param filters Mapa con campo->valor para filtrar
     * @return Lista de registros que cumplen todos los filtros
     */
    public List<Map<String, Object>> filterByFields(String sheetName, Map<String, Object> filters) {
        return getData(sheetName).stream()
                .filter(row -> {
                    for (Map.Entry<String, Object> filter : filters.entrySet()) {
                        Object rowValue = row.get(filter.getKey());
                        Object filterValue = filter.getValue();
                        if (filterValue != null && !filterValue.equals(rowValue)) {
                            return false;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene estadisticas de los datos cargados
     */
    public Map<String, Object> getDataStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSheets", data.size());
        
        Map<String, Integer> recordCounts = new HashMap<>();
        for (Map.Entry<String, List<Map<String, Object>>> entry : data.entrySet()) {
            recordCounts.put(entry.getKey(), entry.getValue().size());
        }
        stats.put("recordCounts", recordCounts);
        
        int totalRecords = data.values().stream()
                .mapToInt(List::size)
                .sum();
        stats.put("totalRecords", totalRecords);
        
        return stats;
    }
    
    /**
     * Verifica si los datos estan cargados
     */
    public boolean isDataLoaded() {
        return !data.isEmpty();
    }
    
    /**
     * Obtiene la lista de hojas disponibles
     */
    public List<String> getAvailableSheets() {
        return new ArrayList<>(data.keySet());
    }
}

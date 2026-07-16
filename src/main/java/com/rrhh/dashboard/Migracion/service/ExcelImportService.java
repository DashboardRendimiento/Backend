package com.rrhh.dashboard.Migracion.service;

import com.rrhh.dashboard.Migracion.Dtos.ExcelRowDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ExcelImportService {

    public List<ExcelRowDTO> readExcelFile(MultipartFile file) throws Exception {
        List<ExcelRowDTO> excelRows = new ArrayList<>();
        List<ExcelRowDTO> empleadosRows = new ArrayList<>();
        List<ExcelRowDTO> productividadRows = new ArrayList<>();
        List<ExcelRowDTO> asistenciaRows = new ArrayList<>();
        List<ExcelRowDTO> resumenRows = new ArrayList<>();
        
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo Excel está vacío");
        }
        
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            
            System.out.println("=== Leyendo hojas del Excel ===");
            
            // 1. Leer hoja Empleados
            Sheet empleadosSheet = workbook.getSheet("Empleados");
            if (empleadosSheet != null) {
                System.out.println("Leyendo hoja: Empleados");
                empleadosRows = readEmpleadosSheet(empleadosSheet);
                System.out.println("  - " + empleadosRows.size() + " empleados encontrados");
            } else {
                System.out.println("No se encontró la hoja 'Empleados'");
            }
            
            // 2. Leer hoja Productividad_Diaria
            Sheet productividadSheet = workbook.getSheet("Productividad_Diaria");
            if (productividadSheet != null) {
                System.out.println("Leyendo hoja: Productividad_Diaria");
                productividadRows = readProductividadSheet(productividadSheet);
                System.out.println("  - " + productividadRows.size() + " registros de productividad");
            } else {
                System.out.println("No se encontró la hoja 'Productividad_Diaria'");
            }
            
            // 3. Leer hoja Asistencia_Diaria
            Sheet asistenciaSheet = workbook.getSheet("Asistencia_Diaria");
            if (asistenciaSheet != null) {
                System.out.println("Leyendo hoja: Asistencia_Diaria");
                asistenciaRows = readAsistenciaSheet(asistenciaSheet);
                System.out.println("  - " + asistenciaRows.size() + " registros de asistencia");
            } else {
                System.out.println("No se encontró la hoja 'Asistencia_Diaria'");
            }
            
            // 4. Leer hoja Resumen_KPIs
            Sheet resumenSheet = workbook.getSheet("Resumen_KPIs");
            if (resumenSheet != null) {
                System.out.println("Leyendo hoja: Resumen_KPIs");
                resumenRows = readResumenSheet(resumenSheet);
                System.out.println("  - " + resumenRows.size() + " registros de resumen");
            } else {
                System.out.println("No se encontró la hoja 'Resumen_KPIs'");
            }
            
            // 5. Combinar todos los datos
            System.out.println("\n=== Combinando datos ===");
            
            // Primero, combinar empleados con resumen
            if (!empleadosRows.isEmpty()) {
                mergeResumenData(empleadosRows, resumenRows);
            }
            
            // Combinar empleados con productividad
            if (!empleadosRows.isEmpty() && !productividadRows.isEmpty()) {
                mergeProductividadData(empleadosRows, productividadRows);
            }
            
            // Combinar empleados con asistencia
            if (!empleadosRows.isEmpty() && !asistenciaRows.isEmpty()) {
                mergeAsistenciaData(empleadosRows, asistenciaRows);
            }
            
            // Si tenemos datos de empleados, esos son los principales
            if (!empleadosRows.isEmpty()) {
                excelRows = empleadosRows;
            } else if (!productividadRows.isEmpty()) {
                excelRows = productividadRows;
            } else if (!asistenciaRows.isEmpty()) {
                excelRows = asistenciaRows;
            } else if (!resumenRows.isEmpty()) {
                excelRows = resumenRows;
            }
        }
        
        if (excelRows.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron datos en las hojas del Excel");
        }
        
        System.out.println("\n=== Total de filas procesadas: " + excelRows.size() + " ===");
        return excelRows;
    }
    
    // ==================== LECTURA DE HOJAS ====================
    
    private List<ExcelRowDTO> readEmpleadosSheet(Sheet sheet) {
        List<ExcelRowDTO> rows = new ArrayList<>();
        
        if (sheet == null || sheet.getPhysicalNumberOfRows() == 0) {
            return rows;
        }
        
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) return rows;
        
        // Mapear columnas de Empleados
        int idEmpleadoCol = -1;
        int nombreCol = -1;
        int apellidoCol = -1;
        int puestoCol = -1;
        int turnoCol = -1;
        
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null) {
                String headerValue = cell.getStringCellValue().toLowerCase().trim();
                System.out.println("  Columna Empleados " + i + ": '" + headerValue + "'");
                
                switch (headerValue) {
                    case "id_empleado": idEmpleadoCol = i; break;
                    case "nombre": nombreCol = i; break;
                    case "puesto": puestoCol = i; break;
                    case "apellidoCol":apellidoCol= i; break;
                    case "turno": turnoCol = i; break;
                }
            }
        }
        
        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null || isRowEmpty(row)) continue;
            
            ExcelRowDTO dto = new ExcelRowDTO();
            dto.setIdEmpleado(getStringValue(row, idEmpleadoCol));
            dto.setNombre(getStringValue(row, nombreCol));
            dto.setPuesto(getStringValue(row, puestoCol));
            dto.setTurno(getStringValue(row, turnoCol));
            
            rows.add(dto);
        }
        
        return rows;
    }
    
    private List<ExcelRowDTO> readProductividadSheet(Sheet sheet) {
        List<ExcelRowDTO> rows = new ArrayList<>();
        
        if (sheet == null || sheet.getPhysicalNumberOfRows() == 0) {
            return rows;
        }
        
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) return rows;
        
        // Mapear columnas de Productividad
        int fechaCol = -1;
        int idEmpleadoCol = -1;
        int nombreCol = -1;
        int horasTrabCol = -1;
        int bultosCol = -1;
        int pedidosPrepCol = -1;
        
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null) {
                String headerValue = cell.getStringCellValue().toLowerCase().trim();
                System.out.println("  Columna Productividad " + i + ": '" + headerValue + "'");
                
                switch (headerValue) {
                    case "fecha": fechaCol = i; break;
                    case "id_empleado": idEmpleadoCol = i; break;
                    case "nombre": nombreCol = i; break;
                    case "horas_trabajadas": horasTrabCol = i; break;
                    case "bultos_preparados": bultosCol = i; break;
                    case "pedidos_preparados": pedidosPrepCol = i; break;
                }
            }
        }
        
        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null || isRowEmpty(row)) continue;
            
            ExcelRowDTO dto = new ExcelRowDTO();
            dto.setFecha(getLocalDateValue(row, fechaCol));
            dto.setIdEmpleado(getStringValue(row, idEmpleadoCol));
            dto.setNombre(getStringValue(row, nombreCol));
            dto.setHorasTrabajadas(getDoubleValue(row, horasTrabCol));
            dto.setBultosPreparados(getIntegerValue(row, bultosCol));
            dto.setPedidosPreparados(getIntegerValue(row, pedidosPrepCol));
            
            rows.add(dto);
        }
        
        return rows;
    }
    
    private List<ExcelRowDTO> readAsistenciaSheet(Sheet sheet) {
        List<ExcelRowDTO> rows = new ArrayList<>();
        
        if (sheet == null || sheet.getPhysicalNumberOfRows() == 0) {
            return rows;
        }
        
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) return rows;
        
        // Mapear columnas de Asistencia
        int fechaCol = -1;
        int idEmpleadoCol = -1;
        int nombreCol = -1;
        int estadoCol = -1;
        int horasTrabCol = -1;
        int minutosTardCol = -1;
        int horasExtraCol = -1;
        
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null) {
                String headerValue = cell.getStringCellValue().toLowerCase().trim();
                System.out.println("  Columna Asistencia " + i + ": '" + headerValue + "'");
                
                switch (headerValue) {
                    case "fecha": fechaCol = i; break;
                    case "id_empleado": idEmpleadoCol = i; break;
                    case "nombre": nombreCol = i; break;
                    case "estado": estadoCol = i; break;
                    case "horas_trabajadas": horasTrabCol = i; break;
                    case "minutos_tardanza": minutosTardCol = i; break;
                    case "horas_extra": horasExtraCol = i; break;
                }
            }
        }
        
        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null || isRowEmpty(row)) continue;
            
            ExcelRowDTO dto = new ExcelRowDTO();
            dto.setFecha(getLocalDateValue(row, fechaCol));
            dto.setIdEmpleado(getStringValue(row, idEmpleadoCol));
            dto.setNombre(getStringValue(row, nombreCol));
            dto.setEstado(getStringValue(row, estadoCol));
            dto.setHorasTrabajadas(getDoubleValue(row, horasTrabCol));
            dto.setMinutosTardanza(getIntegerValue(row, minutosTardCol));
            dto.setHorasExtra(getDoubleValue(row, horasExtraCol));
            
            rows.add(dto);
        }
        
        return rows;
    }
    
    private List<ExcelRowDTO> readResumenSheet(Sheet sheet) {
        List<ExcelRowDTO> rows = new ArrayList<>();
        
        if (sheet == null || sheet.getPhysicalNumberOfRows() == 0) {
            return rows;
        }
        
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) return rows;
        
        // Mapear columnas de Resumen_KPIs
        int idEmpleadoCol = -1;
        int nombreCol = -1;
        int puestoCol = -1;
        int turnoCol = -1;
        int apellidoCol = -1;

        
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null) {
                String headerValue = cell.getStringCellValue().toLowerCase().trim();
                System.out.println("  Columna Resumen " + i + ": '" + headerValue + "'");
                
                switch (headerValue) {
                    case "id_empleado": idEmpleadoCol = i; break;
                    case "nombre": nombreCol = i; break;
                    case "puesto": puestoCol = i; break;
                    case "turno": turnoCol = i; break;
                    case "apellido": apellidoCol = i; break;
                }
            }
        }
        
        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null || isRowEmpty(row)) continue;
            
            ExcelRowDTO dto = new ExcelRowDTO();
            dto.setIdEmpleado(getStringValue(row, idEmpleadoCol));
            dto.setNombre(getStringValue(row, nombreCol));
            dto.setPuesto(getStringValue(row, puestoCol));
            dto.setTurno(getStringValue(row, turnoCol));
            dto.setApellido(getStringValue(row, apellidoCol));
            
            rows.add(dto);
        }
        
        return rows;
    }
    
    // ==================== COMBINACIÓN DE DATOS ====================
    
    private void mergeResumenData(List<ExcelRowDTO> mainRows, List<ExcelRowDTO> resumenRows) {
        for (ExcelRowDTO main : mainRows) {
            for (ExcelRowDTO resumen : resumenRows) {
                if (main.getDni() != null && resumen.getDni() != null &&
                    main.getDni().equals(resumen.getDni())) {
                    
                    if (main.getPuesto() == null) main.setPuesto(resumen.getPuesto());
                    if (main.getTurno() == null) main.setTurno(resumen.getTurno());
                    break;
                }
            }
        }
    }
    
    private void mergeProductividadData(List<ExcelRowDTO> mainRows, List<ExcelRowDTO> productividadRows) {
        for (ExcelRowDTO main : mainRows) {
            for (ExcelRowDTO prod : productividadRows) {
                if (main.getDni() != null && prod.getDni() != null &&
                    main.getDni().equals(prod.getDni())) {
                    
                    main.setBultosPreparados(prod.getBultosPreparados());
                    main.setPedidosPreparados(prod.getPedidosPreparados());
                    main.setHorasTrabajadas(prod.getHorasTrabajadas());
                    break;
                }
            }
        }
    }
    
    private void mergeAsistenciaData(List<ExcelRowDTO> mainRows, List<ExcelRowDTO> asistenciaRows) {
        for (ExcelRowDTO main : mainRows) {
            for (ExcelRowDTO asis : asistenciaRows) {
                if (main.getDni() != null && asis.getDni() != null &&
                    main.getDni().equals(asis.getDni())) {
                    
                    main.setEstado(asis.getEstado());
                    main.setMinutosTardanza(asis.getMinutosTardanza());
                    main.setHorasExtra(asis.getHorasExtra());
                    break;
                }
            }
        }
    }
    
    // ==================== MÉTODOS AUXILIARES ====================
    
    private boolean isRowEmpty(Row row) {
        if (row == null) return true;
        
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = cell.toString().trim();
                if (!value.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private String getStringValue(Row row, int colIndex) {
        if (colIndex == -1) return null;
        Cell cell = row.getCell(colIndex);
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
                default: 
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
    
    private Integer getIntegerValue(Row row, int colIndex) {
        if (colIndex == -1) return null;
        Cell cell = row.getCell(colIndex);
        if (cell == null) return null;
        
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return (int) cell.getNumericCellValue();
            } else if (cell.getCellType() == CellType.STRING) {
                String value = cell.getStringCellValue().trim();
                return value.isEmpty() ? null : Integer.parseInt(value);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    private Double getDoubleValue(Row row, int colIndex) {
        if (colIndex == -1) return null;
        Cell cell = row.getCell(colIndex);
        if (cell == null) return null;
        
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return cell.getNumericCellValue();
            } else if (cell.getCellType() == CellType.STRING) {
                String value = cell.getStringCellValue().trim();
                return value.isEmpty() ? null : Double.parseDouble(value);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    private LocalDate getLocalDateValue(Row row, int colIndex) {
        if (colIndex == -1) return null;
        Cell cell = row.getCell(colIndex);
        if (cell == null) return null;
        
        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                Date date = cell.getDateCellValue();
                return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            } else if (cell.getCellType() == CellType.STRING) {
                String value = cell.getStringCellValue().trim();
                if (!value.isEmpty()) {
                    // Intentar parsear formato yyyy-MM-dd
                    try {
                        return LocalDate.parse(value);
                    } catch (Exception e) {
                        // Intentar formato dd/MM/yyyy
                        try {
                            String[] parts = value.split("/");
                            if (parts.length == 3) {
                                int day = Integer.parseInt(parts[0]);
                                int month = Integer.parseInt(parts[1]);
                                int year = Integer.parseInt(parts[2]);
                                return LocalDate.of(year, month, day);
                            }
                        } catch (Exception ex) {
                            return null;
                        }
                    }
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
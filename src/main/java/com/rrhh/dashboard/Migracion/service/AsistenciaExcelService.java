package com.rrhh.dashboard.Migracion.service;

import com.rrhh.dashboard.Productividad.Entity.AsistenciaDiaria;
import com.rrhh.dashboard.Productividad.repository.AsistenciaRepo;
import com.rrhh.dashboard.Empleados.Entity.Empleados;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsistenciaExcelService {
    
    private final AsistenciaRepo asistenciaRepo;
    
    public List<AsistenciaDiaria> procesarExcel(MultipartFile file) {
        List<AsistenciaDiaria> asistencias = new ArrayList<>();
        
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            int rowNum = 0;
            
            // Saltar encabezados
            if (rows.hasNext()) {
                rows.next();
                rowNum++;
            }
            
            while (rows.hasNext()) {
                Row row = rows.next();
                try {
                    AsistenciaDiaria asistencia = mapearRowAAsistencia(row);
                    asistencias.add(asistencia);
                    
                } catch (Exception e) {
                    log.error("Error en fila {} de Asistencia: {}", rowNum, e.getMessage());
                }
                rowNum++;
            }
            
        } catch (Exception e) {
            log.error("Error procesando archivo Excel", e);
            throw new RuntimeException("Error al procesar el archivo", e);
        }
        
        return asistencias;
    }
    
    public int guardarAsistencias(List<AsistenciaDiaria> asistencias) {
        if (!asistencias.isEmpty()) {
            return asistenciaRepo.saveAll(asistencias).size();
        }
        return 0;
    }
    
    private AsistenciaDiaria mapearRowAAsistencia(Row row) {
        String idEmpleado = getStringValue(row.getCell(0));
        String nombre = getStringValue(row.getCell(1)); 
        String estado = getStringValue(row.getCell(2)); 
        
        List<Empleados> empleados = asistenciaRepo.findByIdEmpleado(idEmpleado);
        Empleados empleado = !empleados.isEmpty() ? empleados.get(0) : null;
        
        return AsistenciaDiaria.builder()
            .idEmpleado(idEmpleado)
            .nombre(nombre)
            .estado(estado)
            .fecha(LocalDate.now()) 
            .horasTrabajadas(getDoubleValue(row.getCell(3))) 
            .minutosTardanza(getIntegerValue(row.getCell(4)))
            .horasExtra(getDoubleValue(row.getCell(5))) 
            .empleado(empleado)
            .build();
    }
    
    private String getStringValue(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            default:
                return null;
        }
    }
    
    private Integer getIntegerValue(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case NUMERIC:
                return (int) cell.getNumericCellValue();
            case STRING:
                try {
                    return Integer.parseInt(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    return null;
                }
            default:
                return null;
        }
    }
    
    private Double getDoubleValue(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                try {
                    return Double.parseDouble(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    return null;
                }
            default:
                return null;
        }
    }
}
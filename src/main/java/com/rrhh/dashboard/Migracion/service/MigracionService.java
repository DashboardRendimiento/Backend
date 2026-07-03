package com.rrhh.dashboard.Migracion.service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.rrhh.dashboard.Empleados.Entity.Empleados;
import com.rrhh.dashboard.Empleados.Repository.EmpleadoRepository;
import com.rrhh.dashboard.Productividad.Entity.AsistenciaDiaria;
import com.rrhh.dashboard.Productividad.Entity.ProductividadDiaria;
import com.rrhh.dashboard.Productividad.repository.AsistenciaRepo;
import com.rrhh.dashboard.Productividad.repository.ProductividadRepository;
import com.rrhh.dashboard.Productividad.repository.ResumenKPIRepo;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class MigracionService {
    
    @Autowired
    private EmpleadoRepository empleadoRepository;
    
    
    @Autowired
    private ExcelService excelService;

    @Autowired
    private ProductividadRepository productividadRepository;
    
    @Autowired
    private AsistenciaRepo asistenciaRepository;
    
    @Autowired
    private ResumenKPIRepo resumenKPIRepository;
    
    public Map<String, Integer> procesarExcelMultiHoja(MultipartFile archivo) throws IOException {
        Map<String, Integer> resultados = new HashMap<>();
        
        try (Workbook workbook = excelService.obtenerWorkbook(archivo)) {
            
            // Obtener las 4 hojas específicas
            Sheet sheetEmpleados = excelService.obtenerHoja(workbook, "Empleados");
            
            // Procesar en orden
            int empleadosCargados = procesarEmpleados(sheetEmpleados);
            resultados.put("empleados", empleadosCargados);


            Sheet sheetProductividad = excelService.obtenerHoja(workbook, "Productividad_Diaria");
            Sheet sheetAsistencia = excelService.obtenerHoja(workbook, "Asistencia_Diaria");

            int productividadCargados = procesarProductividad(sheetProductividad);
            resultados.put("productividad", productividadCargados);
            
            int asistenciaCargados = procesarAsistencia(sheetAsistencia);
            resultados.put("asistencia", asistenciaCargados);
            
    
        } catch (Exception e) {
            log.error("Error procesando archivo Excel", e);
            throw new RuntimeException("Error al procesar el archivo: " + e.getMessage(), e);
        }
        
        return resultados;
    }
    
    // ================ EMPLEADOS ================
    private int procesarEmpleados(Sheet sheet) {
        if (sheet == null) {
            log.warn("Hoja Empleados no encontrada");
            return 0;
        }
        
        List<Empleados> empleados = new ArrayList<>();
        Iterator<Row> rows = excelService.obtenerFilas(sheet);
        
        // Saltar cabecera
        excelService.saltarCabecera(rows);
        
        int rowNum = 1;
        while (rows.hasNext()) {
            Row row = rows.next();
            try {
                Empleados empleado = new Empleados();
                empleado.setIdEmpleado(excelService.getIntegerValue(row.getCell(1)));
                empleado.setNombre(excelService.getStringValue(row.getCell(1)));
                empleado.setPuesto(excelService.getStringValue(row.getCell(2)));
                empleado.setTurno(excelService.getStringValue(row.getCell(3)));
                empleados.add(empleado);
                
            } catch (Exception e) {
                log.error("Error en fila {} de Empleados: {}", rowNum, e.getMessage());
            }
            rowNum++;
        }
        
        if (!empleados.isEmpty()) {
            return empleadoRepository.saveAll(empleados).size();
        }
        return 0;
    }

    // ================ PRODUCTIVIDAD_DIARIA ================
    private int procesarProductividad(Sheet sheet) {
        if (sheet == null) {
            log.warn("Hoja Productividad_Diaria no encontrada");
            return 0;
        }
        
        List<ProductividadDiaria> productividades = new ArrayList<>();
        Iterator<Row> rows = excelService.obtenerFilas(sheet);
        
        excelService.saltarCabecera(rows);
        
        int rowNum = 1;
        while (rows.hasNext()) {
            Row row = rows.next();
            try {
                
                ProductividadDiaria prod = new ProductividadDiaria();
                prod.setFecha(excelService.getDateValue(row.getCell(5)));
                
                productividades.add(prod);
                
            } catch (Exception e) {
                log.error("Error en fila {} de Productividad: {}", rowNum, e.getMessage());
            }
            rowNum++;
        }
        
        if (!productividades.isEmpty()) {
            return productividadRepository.saveAll(productividades).size();
        }
        return 0;
    }
    
    // ================ ASISTENCIA_DIARIA ================
    private int procesarAsistencia(Sheet sheet) {
        if (sheet == null) {
            log.warn("Hoja Asistencia_Diaria no encontrada");
            return 0;
        }
        
        List<AsistenciaDiaria> asistencias = new ArrayList<>();
        Iterator<Row> rows = excelService.obtenerFilas(sheet);
        
        excelService.saltarCabecera(rows);
        
        int rowNum = 1;
        while (rows.hasNext()) {
            Row row = rows.next();
            try {
                String idEmpleado = excelService.getStringValue(row.getCell(0));
                
                AsistenciaDiaria asistencia = new AsistenciaDiaria();
                
                // Usando los campos que existen en tu entidad
                asistencia.setIdEmpleado(idEmpleado);
                asistencia.setNombre(excelService.getStringValue(row.getCell(1))); // Ajusta según tu Excel
                asistencia.setEstado(excelService.getStringValue(row.getCell(2))); // Ajusta según tu Excel
                asistencia.setFecha(excelService.getDateValue(row.getCell(3))); // Ajusta según tu Excel
                asistencia.setHorasTrabajadas(excelService.getDoubleValue(row.getCell(4))); // Ajusta según tu Excel
                asistencia.setMinutosTardanza(excelService.getIntegerValue(row.getCell(5))); // Ajusta según tu Excel
                asistencia.setHorasExtra(excelService.getDoubleValue(row.getCell(6))); // Ajusta según tu Excel
                

                
            } catch (Exception e) {
                log.error("Error en fila {} de Asistencia: {}", rowNum, e.getMessage());
            }
            rowNum++;
        }
        
        if (!asistencias.isEmpty()) {
            return asistenciaRepository.saveAll(asistencias).size();
        }
        return 0;
    }
    /*
        // ================ RESUMEN_KPIs ================
    private int procesarResumenKPIs(Sheet sheet) {
        if (sheet == null) {
            log.warn("Hoja Resumen_KPIs no encontrada");
            return 0;
        }
        
        List<ResumenKpi> kpis = new ArrayList<>();
        Iterator<Row> rows = excelService.obtenerFilas(sheet);
        
        excelService.saltarCabecera(rows);
        
        while (rows.hasNext()) {
            Row row = rows.next();
            try {
                ResumenKpi kpi = new ResumenKpi();
                // map fields from Excel to kpi entity as needed
                kpis.add(kpi);
                
            } catch (Exception e) {
                log.error("Error en fila de Resumen_KPIs: {}", e.getMessage());
            }
        }
        
        if (!kpis.isEmpty()) {
            List<ResumenKpi> saved = resumenKPIRepository.saveAll(kpis);
            return saved.size();
        }
        return 0;
    }
    */
}
package com.rrhh.dashboard.service;

import com.rrhh.dashboard.Empleados.dtos.EmpleadoDTO;
import com.rrhh.dashboard.Migracion.Dtos.AsistenciaDiariaDTO;
import com.rrhh.dashboard.Migracion.Dtos.ProductividadDiariaDTO;
import com.rrhh.dashboard.Migracion.Dtos.ResumenKpiDTO;


import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
@Slf4j
public class ExcelDataService {

    @Value("${app.excel.file}")
    private Resource excelFile;

    // =========================
    // DATOS EN MEMORIA
    // =========================
    private List<ResumenKpiDTO> resumen = new ArrayList<>();
    private List<EmpleadoDTO> empleados = new ArrayList<>();
    private List<ProductividadDiariaDTO> productividad = new ArrayList<>();
    private List<AsistenciaDiariaDTO> asistencia = new ArrayList<>();

    // =========================
    // CARGA INICIAL
    // =========================
    @PostConstruct
    public void loadExcelData() {
        log.info("🔄 Iniciando carga de Excel...");

        try (InputStream inputStream = excelFile.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            this.resumen = parseResumen(workbook.getSheet("Resumen_KPIs"));
            this.empleados = parseEmpleados(workbook.getSheet("Empleados"));
            this.productividad = parseProductividad(workbook.getSheet("Productividad_Diaria"));
            this.asistencia = parseAsistencia(workbook.getSheet("Asistencia_Diaria"));

            log.info("✅ Excel cargado correctamente");
            log.info("📊 Empleados: {}", empleados.size());
            log.info("📊 Productividad: {}", productividad.size());

        } catch (Exception e) {
            log.error("❌ Error cargando Excel", e);
        }
    }

    // =========================
    // PARSER: RESUMEN KPI
    // =========================
    private List<ResumenKpiDTO> parseResumen(Sheet sheet) {
        if (sheet == null) return List.of();

        List<ResumenKpiDTO> list = new ArrayList<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            ResumenKpiDTO dto = new ResumenKpiDTO();

            dto.setIndicador(getString(row.getCell(0)));
            dto.setValorActual(getDouble(row.getCell(1)));
            dto.setMeta(getDouble(row.getCell(2)));
            dto.setPorcentajeCumplimiento(getDouble(row.getCell(3)));
            dto.setEstado(getString(row.getCell(4)));

            list.add(dto);
        }

        return list;
    }

    // =========================
    // PARSER: EMPLEADOS
    // =========================
    private List<EmpleadoDTO> parseEmpleados(Sheet sheet) {
        if (sheet == null) return List.of();

        List<EmpleadoDTO> list = new ArrayList<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            EmpleadoDTO dto = new EmpleadoDTO();

            dto.setIdEmpleado(getString(row.getCell(0)));
            dto.setNombre(getString(row.getCell(1)));
            dto.setApellido(getString(row.getCell(2)));
            dto.setSector(getString(row.getCell(3)));
            dto.setPuesto(getString(row.getCell(4)));
            dto.setSupervisor(getString(row.getCell(5)));
            dto.setTurno(getString(row.getCell(6)));
            dto.setEstado(getString(row.getCell(7)));
            dto.setFechaIngreso(getString(row.getCell(8)));

            list.add(dto);
        }

        return list;
    }

    // =========================
    // PARSER: PRODUCTIVIDAD
    // =========================
    private List<ProductividadDiariaDTO> parseProductividad(Sheet sheet) {
        if (sheet == null) return List.of();

        List<ProductividadDiariaDTO> list = new ArrayList<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            ProductividadDiariaDTO dto = new ProductividadDiariaDTO();

            dto.setIdEmpleado(getString(row.getCell(0)));
            dto.setFecha(getLocalDate(row.getCell(1)));
            dto.setPedidosProcesados(getInt(row.getCell(2)));
            dto.setPedidosEsperados(getInt(row.getCell(3)));
            dto.setProductividad(getDouble(row.getCell(4)));
            dto.setErrores(getInt(row.getCell(5)));
            dto.setEficiencia(getDouble(row.getCell(6)));

            list.add(dto);
        }

        return list;
    }

    // =========================
    // PARSER: ASISTENCIA
    // =========================
    private List<AsistenciaDiariaDTO> parseAsistencia(Sheet sheet) {
        if (sheet == null) return List.of();

        List<AsistenciaDiariaDTO> list = new ArrayList<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            AsistenciaDiariaDTO dto = new AsistenciaDiariaDTO();

            dto.setIdEmpleado(getString(row.getCell(0)));
            dto.setFecha(getLocalDate(row.getCell(1)));
            dto.setEstado(getString(row.getCell(2)));
            dto.setHoraEntrada(getString(row.getCell(3)));
            dto.setHoraSalida(getString(row.getCell(4)));
            dto.setMinutosTardanza(getInt(row.getCell(5)));

            list.add(dto);
        }

        return list;
    }

    // =========================
    // HELPERS
    // =========================
    private String getString(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            default -> null;
        };
    }

    private Double getDouble(Cell cell) {
        if (cell == null) return null;
        return cell.getCellType() == CellType.NUMERIC ? cell.getNumericCellValue() : null;
    }

    private Integer getInt(Cell cell) {
        if (cell == null) return null;
        return cell.getCellType() == CellType.NUMERIC ? (int) cell.getNumericCellValue() : null;
    }

    private Boolean getBoolean(Cell cell) {
        if (cell == null) return null;
        return cell.getCellType() == CellType.BOOLEAN ? cell.getBooleanCellValue() : null;
    }

    private LocalDate getLocalDate(Cell cell) {
    if (cell == null) return null;

    try {
        switch (cell.getCellType()) {

            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue()
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                }
                return null;
            }

            case STRING -> {
                String value = cell.getStringCellValue();
                if (value == null || value.isBlank()) return null;

                // intenta formato común yyyy-MM-dd o dd/MM/yyyy
                if (value.contains("-")) {
                    return LocalDate.parse(value);
                } else if (value.contains("/")) {
                    String[] parts = value.split("/");
                    return LocalDate.of(
                            Integer.parseInt(parts[2]),
                            Integer.parseInt(parts[1]),
                            Integer.parseInt(parts[0])
                    );
                }

                return null;
            }

            default -> {
                return null;
            }
        }
    } catch (Exception e) {
        return null; // evita romper el startup
    }
}

    // =========================
    // GETTERS PÚBLICOS
    // =========================
    public List<EmpleadoDTO> getEmpleados() {
        return empleados;
    }

    public List<ProductividadDiariaDTO> getProductividad() {
        return productividad;
    }

    public List<AsistenciaDiariaDTO> getAsistencia() {
        return asistencia;
    }

 
    public List<ResumenKpiDTO> getResumen() {
        return resumen;
    }

    // =========================
    // ESTADO DEL SISTEMA
    // =========================
    public boolean isDataLoaded() {
        return !empleados.isEmpty();
    }
}
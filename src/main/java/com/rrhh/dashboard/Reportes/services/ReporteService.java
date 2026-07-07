package com.rrhh.dashboard.Reportes.services;

import com.rrhh.dashboard.Asistencia.Entity.AttendanceRecord;
import com.rrhh.dashboard.Asistencia.Repository.AttendanceRecordRepository;
import com.rrhh.dashboard.Empleados.Entity.Empleados;
import com.rrhh.dashboard.Productividad.Entity.ProductividadDiaria;
import com.rrhh.dashboard.Productividad.repository.ProductividadRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Exportacion de informes para el rol SUPERVISOR (y Administrador/SuperAdmin):
 * Excel via Apache POI (misma libreria que ya usa Migracion) y CSV simple,
 * sin dependencias nuevas.
 */
@Service
public class ReporteService {

    private final ProductividadRepository productividadRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;

    public ReporteService(ProductividadRepository productividadRepository,
                           AttendanceRecordRepository attendanceRecordRepository) {
        this.productividadRepository = productividadRepository;
        this.attendanceRecordRepository = attendanceRecordRepository;
    }

    public byte[] productividadExcel() {
        List<ProductividadDiaria> registros = productividadRepository.findAll();
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Productividad");
            Row header = sheet.createRow(0);
            String[] columnas = {"Empleado", "Fecha", "Pedidos Encargados", "Pedidos Preparados",
                    "Pedidos Pendientes", "Bultos Preparados"};
            for (int i = 0; i < columnas.length; i++) {
                header.createCell(i).setCellValue(columnas[i]);
            }

            int rowIndex = 1;
            for (ProductividadDiaria p : registros) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(nombreCompleto(p.getEmpleado()));
                row.createCell(1).setCellValue(p.getFecha() != null ? p.getFecha().toString() : "");
                row.createCell(2).setCellValue(valorODefault(p.getPedidosEncargados()));
                row.createCell(3).setCellValue(valorODefault(p.getPedidosPreparados()));
                row.createCell(4).setCellValue(valorODefault(p.getPedidosPendientes()));
                row.createCell(5).setCellValue(valorODefault(p.getBultosPreparados()));
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("Error generando el Excel de productividad", e);
        }
    }

    public byte[] productividadCsv() {
        StringBuilder csv = new StringBuilder("Empleado,Fecha,Pedidos Encargados,Pedidos Preparados,Pedidos Pendientes,Bultos Preparados\n");
        for (ProductividadDiaria p : productividadRepository.findAll()) {
            csv.append(csvField(nombreCompleto(p.getEmpleado()))).append(',')
                    .append(p.getFecha() != null ? p.getFecha() : "").append(',')
                    .append(valorODefault(p.getPedidosEncargados())).append(',')
                    .append(valorODefault(p.getPedidosPreparados())).append(',')
                    .append(valorODefault(p.getPedidosPendientes())).append(',')
                    .append(valorODefault(p.getBultosPreparados())).append('\n');
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] asistenciaExcel() {
        List<AttendanceRecord> registros = attendanceRecordRepository.findAll();
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Asistencia");
            Row header = sheet.createRow(0);
            String[] columnas = {"Empleado ID", "Entrada", "Salida"};
            for (int i = 0; i < columnas.length; i++) {
                header.createCell(i).setCellValue(columnas[i]);
            }

            int rowIndex = 1;
            for (AttendanceRecord a : registros) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(a.getEmployeeId());
                row.createCell(1).setCellValue(a.getClockInAt() != null ? a.getClockInAt().toString() : "");
                row.createCell(2).setCellValue(a.getClockOutAt() != null ? a.getClockOutAt().toString() : "");
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("Error generando el Excel de asistencia", e);
        }
    }

    public byte[] asistenciaCsv() {
        StringBuilder csv = new StringBuilder("Empleado ID,Entrada,Salida\n");
        for (AttendanceRecord a : attendanceRecordRepository.findAll()) {
            csv.append(a.getEmployeeId()).append(',')
                    .append(a.getClockInAt() != null ? a.getClockInAt() : "").append(',')
                    .append(a.getClockOutAt() != null ? a.getClockOutAt() : "").append('\n');
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String nombreCompleto(Empleados empleado) {
        if (empleado == null) {
            return "";
        }
        return (empleado.getNombre() != null ? empleado.getNombre() : "")
                + " " + (empleado.getApellido() != null ? empleado.getApellido() : "");
    }

    private int valorODefault(Integer valor) {
        return valor != null ? valor : 0;
    }

    private String csvField(String valor) {
        return "\"" + valor.replace("\"", "\"\"") + "\"";
    }
}

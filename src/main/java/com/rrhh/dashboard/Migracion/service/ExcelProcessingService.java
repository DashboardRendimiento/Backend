package com.rrhh.dashboard.Migracion.service;

import com.rrhh.dashboard.Empleados.Entity.Empleados;
import com.rrhh.dashboard.Empleados.Repository.EmpleadoRepository;
import com.rrhh.dashboard.Migracion.Dtos.ExcelRowDTO;
import com.rrhh.dashboard.registro_productividad.Entity.AsistenciaDiaria;
import com.rrhh.dashboard.registro_productividad.Entity.registro_productividad;
import com.rrhh.dashboard.registro_productividad.repository.AsistenciaRepo;
import com.rrhh.dashboard.registro_productividad.repository.ProductividadRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class ExcelProcessingService {

    private final EmpleadoRepository empleadosRepository;
    private final ProductividadRepository productividadRepository;
    private final AsistenciaRepo asistenciaRepository;
    public ExcelProcessingService(EmpleadoRepository empleadosRepository, ProductividadRepository productividadRepository, AsistenciaRepo asistenciaRepository) {
        this.empleadosRepository = empleadosRepository;
        this.productividadRepository = productividadRepository;
        this.asistenciaRepository = asistenciaRepository;
    }


    @Transactional
    public void processExcelData(List<ExcelRowDTO> excelRows) {
        for (ExcelRowDTO row : excelRows) {
            // 1. Procesar empleado
            Empleados empleado = processEmpleado(row);
            
            // 2. Procesar productividad (si hay datos)
            if (hasProductividadData(row)) {
                processProductividad(row, empleado);
            }
            
            // 3. Procesar asistencia (si hay datos)
            if (hasAsistenciaData(row)) {
                processAsistencia(row, empleado);
            }
        }
    }

    private Empleados processEmpleado(ExcelRowDTO row) {
        // Si no hay nombre, no podemos procesar el empleado
        if (row.getNombre() == null) {
            return null;
        }
        
        // Buscar empleado existente por nombre y apellido
        Optional<Empleados> existingEmpleado = empleadosRepository.findByNombreAndApellido(
            row.getNombre(), 
            row.getApellido() != null ? row.getApellido() : ""
        );
        
        if (existingEmpleado.isPresent()) {
            Empleados empleado = existingEmpleado.get();
            // Actualizar datos si es necesario
            if (row.getSector() != null) empleado.setSector(row.getSector());
            if (row.getPuesto() != null) empleado.setPuesto(row.getPuesto());
            if (row.getTurno() != null) empleado.setTurno(row.getTurno());
            if (row.getDni() != null) empleado.setDni(row.getDni());  // Actualizar DNI si viene
            if (row.getApellido() != null) empleado.setApellido(row.getApellido());
            return empleadosRepository.save(empleado);
        } else {
            // Crear nuevo empleado
            Empleados nuevoEmpleado = new Empleados();
            nuevoEmpleado.setNombre(row.getNombre());
            nuevoEmpleado.setApellido(row.getApellido() != null ? row.getApellido() : "Sin apellido");
            nuevoEmpleado.setSector(row.getSector());
            nuevoEmpleado.setPuesto(row.getPuesto());
            nuevoEmpleado.setTurno(row.getTurno());
            
            // Si tiene DNI, usarlo, sino generar uno automático
            if (row.getDni() != null) {
                nuevoEmpleado.setDni(row.getDni());
            } else {
                nuevoEmpleado.setDni(generateAutoDni());
            }
            
            return empleadosRepository.save(nuevoEmpleado);
        }
    }

    private Long generateAutoDni() {
        // Genera un número entre 10,000,000 y 99,999,999
        Random random = new Random();
        return 10000000L + random.nextLong(90000000);
    }

    private void processProductividad(ExcelRowDTO row, Empleados empleado) {
        if (empleado == null) {
            empleado = getOrCreateGenericEmpleado();
        }
        
        registro_productividad productividad = new registro_productividad();
        productividad.setEmpleado(empleado);
        productividad.setFecha(row.getFecha() != null ? row.getFecha() : LocalDate.now());
        productividad.setBultosPreparados(row.getBultosPreparados());
        productividad.setPedidosPreparados(row.getPedidosPreparados());

        productividadRepository.save(productividad);
    }

    private void processAsistencia(ExcelRowDTO row, Empleados empleado) {
        if (empleado == null) {
            empleado = getOrCreateGenericEmpleado();
        }
        
        AsistenciaDiaria asistencia = new AsistenciaDiaria();
        asistencia.setEmpleado(empleado);
        asistencia.setFecha(row.getFecha() != null ? row.getFecha() : LocalDate.now());
        asistencia.setIdEmpleado(row.getIdEmpleado() != null ? row.getIdEmpleado() : "SIN-ID");
        asistencia.setNombre(row.getNombre() != null ? row.getNombre() : "Sin nombre");
        asistencia.setEstado(row.getEstado());
        asistencia.setHorasTrabajadas(row.getHorasTrabajadas());
        asistencia.setMinutosTardanza(row.getMinutosTardanza());
        asistencia.setHorasExtra(row.getHorasExtra());
        
        asistenciaRepository.save(asistencia);
    }

    private Empleados getOrCreateGenericEmpleado() {
        Optional<Empleados> genericEmpleado = empleadosRepository.findByNombreAndApellido("GENERICO", "EMPLEADO");
        
        if (genericEmpleado.isPresent()) {
            return genericEmpleado.get();
        } else {
            Empleados nuevo = new Empleados();
            nuevo.setNombre("GENERICO");
            nuevo.setApellido("EMPLEADO");
            nuevo.setDni(99999999L);
            nuevo.setSector("General");
            nuevo.setPuesto("Empleado Genérico");
            nuevo.setTurno("Mañana");
            return empleadosRepository.save(nuevo);
        }
    }

    private boolean hasProductividadData(ExcelRowDTO row) {
        return row.getBultosPreparados() != null || 
               row.getPedidosPreparados() != null ;
        }

    private boolean hasAsistenciaData(ExcelRowDTO row) {
        return row.getEstado() != null ||
               row.getHorasTrabajadas() != null ||
               row.getMinutosTardanza() != null ||
               row.getHorasExtra() != null;
    }
}
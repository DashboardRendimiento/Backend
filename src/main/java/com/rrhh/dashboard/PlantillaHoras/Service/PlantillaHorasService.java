package com.rrhh.dashboard.PlantillaHoras.Service;

import com.rrhh.dashboard.Empleados.Entity.Empleados;
import com.rrhh.dashboard.Empleados.services.EmpleadoService;
import com.rrhh.dashboard.PlantillaHoras.Dtos.PlantillaHorasDTO;
import com.rrhh.dashboard.PlantillaHoras.Entity.PlantillaHoras;
import com.rrhh.dashboard.PlantillaHoras.Repository.PlantillaHorasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PlantillaHorasService {

    private final PlantillaHorasRepository repository;
    private final EmpleadoService empleadoService;

    public PlantillaHorasDTO guardar(PlantillaHorasDTO dto) {
        Empleados empleado = empleadoService.buscarPorId(dto.getEmpleadoId())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con id: " + dto.getEmpleadoId()));

        PlantillaHoras plantilla = repository.findByEmpleadoIdAndFecha(dto.getEmpleadoId(), dto.getFecha())
                .orElse(new PlantillaHoras());

        plantilla.setEmpleado(empleado);
        plantilla.setFecha(dto.getFecha());
        plantilla.setHorasTrabajadas(dto.getHorasTrabajadas() != null ? dto.getHorasTrabajadas() : 0.0);
        plantilla.setHorasExtra(dto.getHorasExtra() != null ? dto.getHorasExtra() : 0.0);
        plantilla.setMinutosTardanza(dto.getMinutosTardanza() != null ? dto.getMinutosTardanza() : 0);

        PlantillaHoras guardada = repository.save(plantilla);
        return mapToDTO(guardada);
    }

    public List<PlantillaHorasDTO> obtenerPorEmpleadoYMes(Long empleadoId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        return repository.findByEmpleadoIdAndFechaBetween(empleadoId, startDate, endDate)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private PlantillaHorasDTO mapToDTO(PlantillaHoras entity) {
        PlantillaHorasDTO dto = new PlantillaHorasDTO();
        dto.setId(entity.getId());
        dto.setEmpleadoId(entity.getEmpleado().getId());
        dto.setFecha(entity.getFecha());
        dto.setHorasTrabajadas(entity.getHorasTrabajadas());
        dto.setHorasExtra(entity.getHorasExtra());
        dto.setMinutosTardanza(entity.getMinutosTardanza());
        return dto;
    }
}

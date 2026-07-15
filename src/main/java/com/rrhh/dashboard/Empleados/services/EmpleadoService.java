package com.rrhh.dashboard.Empleados.services;

import com.rrhh.dashboard.Empleados.Entity.Empleados;
import com.rrhh.dashboard.Empleados.Repository.EmpleadoRepository;

import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EmpleadoService {

    private final EmpleadoRepository repository;
    private final PasswordEncoder passwordEncoder;

    public EmpleadoService(EmpleadoRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

 

    public List<Empleados> listar() {
        return repository.findAll();
    }

   public Optional<Empleados> buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new RuntimeException("El ID debe ser un número positivo");
        }
        Optional<Empleados> empleado = repository.findById(id);
        if (empleado.isEmpty()) {
            throw new RuntimeException("No se encontró ningún empleado con el ID: " + id);
        }
        return empleado;
    }
    public List<Empleados> buscarPorSector(String sector) {
        if (sector == null || sector.isBlank()) {
            throw new RuntimeException("El sector no puede estar vacío");
        }
        List<Empleados> empleados = repository.findBySector(sector);
        if (empleados.isEmpty()) {
            throw new RuntimeException("No se encontraron empleados en el sector: " + sector);
        }
        return empleados;
    }

    public List<Empleados> buscarPorPuesto(String puesto) {
        if (puesto == null || puesto.isBlank()) {
            throw new RuntimeException("El puesto no puede estar vacío");
        }
        List<Empleados> empleados = repository.findByPuesto(puesto);
        if (empleados.isEmpty()) {
            throw new RuntimeException("No se encontraron empleados con el puesto: " + puesto);
        }
        return empleados;
    }

    public Long contarPorSector(String sector) {
        if (sector == null || sector.isBlank()) {
            throw new RuntimeException("El sector no puede estar vacío");
        }
        Long cantidad = repository.countBySector(sector);
        if (cantidad == 0) {
            throw new RuntimeException("No se encontraron empleados en el sector: " + sector);
        }
        return cantidad;
    }

    public Long contarPorPuesto(String puesto) {
        if (puesto == null || puesto.isBlank()) {
            throw new RuntimeException("El puesto no puede estar vacío");
        }
        Long cantidad = repository.countByPuesto(puesto);
        if (cantidad == 0) {
            throw new RuntimeException("No se encontraron empleados con el puesto: " + puesto);
        }
        return cantidad;
    }

    public List<Empleados> buscarPorDni(Long dni) {
        if (dni == null) {
            throw new RuntimeException("El DNI no puede ser nulo");
        }
        if (dni.toString().length() < 7 || dni.toString().length() > 8) {
            throw new RuntimeException("El DNI debe tener entre 7 y 8 dígitos");
        }
        List<Empleados> empleados = repository.findByDni(dni);
        if (empleados.isEmpty()) {
            throw new RuntimeException("No se encontraron empleados con el DNI: " + dni);
        }
        return empleados;
    }

    public List<Empleados> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new RuntimeException("El nombre no puede estar vacío");
        }
        if (nombre.length() < 2) {
            throw new RuntimeException("El nombre debe tener al menos 2 caracteres");
        }
        List<Empleados> empleados = repository.findByNombreContainingIgnoreCase(nombre);
        if (empleados.isEmpty()) {
            throw new RuntimeException("No se encontraron empleados con el nombre: " + nombre);
        }
        return empleados;
    }

    public List<Empleados> buscarPorApellido(String apellido) {
        if (apellido == null || apellido.isBlank()) {
            throw new RuntimeException("El apellido no puede estar vacío");
        }
        if (apellido.length() < 2) {
            throw new RuntimeException("El apellido debe tener al menos 2 caracteres");
        }
        List<Empleados> empleados = repository.findByApellidoContainingIgnoreCase(apellido);
        if (empleados.isEmpty()) {
            throw new RuntimeException("No se encontraron empleados con el apellido: " + apellido);
        }
        return empleados;
    }

    public Long totalEmpleados() {
        Long total = repository.count();
        if (total == 0) {
            throw new RuntimeException("No hay empleados registrados en el sistema");
        }
        return total;
    }
    
    @Transactional
    public Empleados guardar(Empleados empleado) {
        if (repository.existsByEmail(empleado.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con el email: " + empleado.getEmail());
        }
        
        if (empleado.getDni() != null && repository.existsByDni(empleado.getDni())) {
            throw new RuntimeException("Ya existe un usuario con el DNI: " + empleado.getDni());
        }
        
        hashPasswordIfPresent(empleado);
        return repository.save(empleado);
    }

    @Transactional
    public Empleados actualizar(Long id, Empleados empleadoActualizado) {
        return repository.findById(id)
                .map(empleado -> {
                    if (!empleado.getEmail().equals(empleadoActualizado.getEmail()) &&
                        repository.existsByEmail(empleadoActualizado.getEmail())) {
                        throw new RuntimeException("Ya existe un usuario con el email: " + empleadoActualizado.getEmail());
                    }
                    
                    if (!empleado.getDni().equals(empleadoActualizado.getDni()) &&
                        repository.existsByDni(empleadoActualizado.getDni())) {
                        throw new RuntimeException("Ya existe un usuario con el DNI: " + empleadoActualizado.getDni());
                    }
                    
                    empleado.setNombre(empleadoActualizado.getNombre());
                    empleado.setApellido(empleadoActualizado.getApellido());
                    empleado.setDni(empleadoActualizado.getDni());
                    empleado.setSector(empleadoActualizado.getSector());
                    empleado.setPuesto(empleadoActualizado.getPuesto());
                    empleado.setTurno(empleadoActualizado.getTurno());
                    empleado.setEmail(empleadoActualizado.getEmail());
                    empleado.setRole(empleadoActualizado.getRole());
                    
                    if (empleadoActualizado.getPasswordHash() != null && 
                        !empleadoActualizado.getPasswordHash().isBlank()) {
                        empleado.setPasswordHash(passwordEncoder.encode(empleadoActualizado.getPasswordHash()));
                    }
                    
                    return repository.save(empleado);
                })
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con ID: " + id));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Empleado no encontrado con ID: " + id);
        }
        repository.deleteById(id);
    }

    private void hashPasswordIfPresent(Empleados empleado) {
        String rawPassword = empleado.getPasswordHash();
        if (rawPassword != null && !rawPassword.isBlank()) {
            empleado.setPasswordHash(passwordEncoder.encode(rawPassword));
        }
    }
}
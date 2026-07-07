package com.rrhh.dashboard.Empleados.services;

import com.rrhh.dashboard.Empleados.Entity.Empleados;
import com.rrhh.dashboard.Empleados.Repository.EmpleadoRepository;
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

    @Transactional
    public Empleados guardar(Empleados empleado) {
        hashPasswordIfPresent(empleado);
        return repository.save(empleado);
    }

    public Optional<Empleados> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public List<Empleados> buscarPorSector(String sector) {
        return repository.findBySector(sector);
    }

    public List<Empleados> buscarPorPuesto(String puesto) {
        return repository.findByPuesto(puesto);
    }

    public Long contarPorSector(String sector) {
        return repository.countBySector(sector);
    }

    public Long contarPorPuesto(String puesto) {
        return repository.countByPuesto(puesto);
    }

    public List<Empleados> buscarPorDni(Long dni) {
        return repository.findByDni(dni);
    }

    public List<Empleados> buscarPorNombre(String nombre) {
        return repository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Empleados> buscarPorApellido(String apellido) {
        return repository.findByApellidoContainingIgnoreCase(apellido);
    }

    @Transactional
    public Empleados actualizar(Long id, Empleados empleadoActualizado) {
        return repository.findById(id)
                .map(empleado -> {
                    empleado.setNombre(empleadoActualizado.getNombre());
                    empleado.setApellido(empleadoActualizado.getApellido());
                    empleado.setSector(empleadoActualizado.getSector());
                    empleado.setPuesto(empleadoActualizado.getPuesto());
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

    public Long totalEmpleados() {
        return repository.count();
    }

    /**
     * El body de creacion trae la contrasena en texto plano en el campo
     * passwordHash (mismo campo que la entidad persiste ya hasheado) — se
     * reemplaza aca por su hash BCrypt antes de guardar, para que nunca
     * quede texto plano en la base.
     */
    private void hashPasswordIfPresent(Empleados empleado) {
        String rawPassword = empleado.getPasswordHash();
        if (rawPassword != null && !rawPassword.isBlank()) {
            empleado.setPasswordHash(passwordEncoder.encode(rawPassword));
        }
    }
}
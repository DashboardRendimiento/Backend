package com.rrhh.dashboard.Empleados;

import com.rrhh.dashboard.Empleados.Entity.Empleados;
import com.rrhh.dashboard.Empleados.Entity.EmployeeRole;
import com.rrhh.dashboard.Empleados.Repository.EmpleadoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Solo el SUPERADMIN puede dar de alta empleados (ver EmpleadoController.crear),
 * por lo que este seeder crea el primer SUPERADMIN si la base está vacía.
 */
@Component
@Slf4j
public class SuperAdminSeeder implements ApplicationRunner {

    private final EmpleadoRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final String email;
    private final String password;

    public SuperAdminSeeder(
            EmpleadoRepository repository,
            PasswordEncoder passwordEncoder,
            @Value("${app.superadmin.email:superadmin@example.com}") String email,
            @Value("${app.superadmin.password:changeme}") String password) {

        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.email = email;
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {

        if (repository.existsByRole(EmployeeRole.SUPERADMIN)) {
            return;
        }

        Empleados superAdmin = new Empleados();
        superAdmin.setNombre("Super");
        superAdmin.setApellido("Admin");
        superAdmin.setDni(99999999L);              
        superAdmin.setSector("Administración");
        superAdmin.setPuesto("Super Administrador");
        superAdmin.setTurno("Mañana");

        superAdmin.setEmail(email);
        superAdmin.setPasswordHash(passwordEncoder.encode(password));
        superAdmin.setRole(EmployeeRole.SUPERADMIN);
        superAdmin.setActive(true);

        repository.save(superAdmin);

        log.warn(
                "No había ningún SUPERADMIN. Se creó uno automáticamente. Email: {}",
                email
        );
    }
}
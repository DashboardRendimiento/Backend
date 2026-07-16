package com.rrhh.dashboard.Empleados;

import com.rrhh.dashboard.Empleados.Entity.Empleados;
import com.rrhh.dashboard.Empleados.Entity.EmployeeRole;
import com.rrhh.dashboard.Empleados.Repository.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class SuperAdminSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SuperAdminSeeder.class);

    private final EmpleadoRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final String email;
    private final String password;

    public SuperAdminSeeder(EmpleadoRepository repository,
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
        superAdmin.setEmail(email);
        superAdmin.setPasswordHash(passwordEncoder.encode(password));
        superAdmin.setRole(EmployeeRole.SUPERADMIN);
        superAdmin.setActive(true);
        repository.save(superAdmin);

        log.warn("No habia ningun SUPERADMIN, se creo uno de arranque. email={} password={} " +
                        "(configurable via app.superadmin.email / app.superadmin.password; cambiar en produccion)",
                email, password);
    }
}

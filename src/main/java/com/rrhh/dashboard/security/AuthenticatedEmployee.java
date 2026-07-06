package com.rrhh.dashboard.security;

import com.rrhh.dashboard.Empleados.Entity.EmployeeRole;
import org.springframework.security.core.AuthenticatedPrincipal;

public record AuthenticatedEmployee(Long employeeId, EmployeeRole role) implements AuthenticatedPrincipal {

    @Override
    public String getName() {
        return employeeId.toString();
    }
}

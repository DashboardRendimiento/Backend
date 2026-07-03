package com.rrhh.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicacion principal del Dashboard RRHH
 * 
 * @author Backend Team
 * @version 1.0.0
 */
@SpringBootApplication
public class DashboardApplication {

    public static void main(String[] args) {
        SpringApplication.run(DashboardApplication.class, args);
        System.out.println("\n" +
                "╔══════════════════════════════════════════════════════════╗\n" +
                "║                                                          ║\n" +
                "║          🚀 Dashboard RRHH API Started!                 ║\n" +
                "║                                                          ║\n" +
                "║          📊 http://localhost:8080                       ║\n" +
                "║          ❤️  Health: http://localhost:8080/api/health   ║\n" +
                "║                                                          ║\n" +
                "╚══════════════════════════════════════════════════════════╝\n");
    }
}

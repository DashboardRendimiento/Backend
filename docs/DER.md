# Diagrama de Entidad-Relación — Dashboard RRHH

Este diagrama usa sintaxis [Mermaid](https://mermaid.js.org/syntax/entityRelationshipDiagram.html)
(`erDiagram`). Se renderiza automáticamente en GitHub y en VS Code (extensión "Markdown Preview
Mermaid Support" u otra equivalente).

```mermaid
erDiagram
    EMPLEADOS {
        Long id PK
        String nombre
        String apellido
        Long dni
        String sector
        String puesto
        String turno
        String email UK
        String passwordHash "hasheado, nunca se expone por la API"
        EmployeeRole role "EMPLEADO, ADMINISTRADOR, SUPERADMIN, SUPERVISOR"
        boolean active
    }

    ATTENDANCE_RECORDS {
        Long id PK
        Long employeeId "sin FK de base, solo el id"
        Instant clockInAt
        Instant clockOutAt
        Instant createdAt
        Instant updatedAt
    }

    WORK_SCHEDULES {
        Long id PK
        Long employeeId UK "sin FK de base, unico por empleado"
        Set_DayOfWeek workDays "tabla aparte work_schedule_days"
        LocalTime startTime
        LocalTime endTime
        Instant createdAt
        Instant updatedAt
    }

    REGISTROS_HORARIOS {
        Long id PK
        Long empleadoId "sin FK de base"
        Integer pedidosPreparados
        Integer bultosPreparados
        Instant registradoEn
    }

    OBJETIVOS {
        Long id PK
        Long empleadoId "sin FK de base"
        TipoObjetivo tipo "PEDIDOS, DINERO"
        Double valorSemanal
        LocalDate semanaInicio "lunes de la semana"
        Instant createdAt
        Instant updatedAt
    }

    PRODUCTIVIDAD_DIARIA {
        Long id PK
        LocalDate fecha
        Integer bultosPreparados
        Integer pedidosPreparados
        Integer pedidosEncargados
        Integer pedidosPendientes
        LocalDateTime fechaCarga
        Long empleado_id FK "FK real (@ManyToOne)"
    }

    ASISTENCIA_DIARIA {
        Long id PK
        LocalDate fecha
        String idEmpleado "legado del import de Excel"
        String nombre
        String estado
        Double horasTrabajadas
        Integer minutosTardanza
        Double horasExtra
        LocalDateTime fechaCarga
        Long empleado_id FK "FK real (@ManyToOne)"
    }

    RESUMEN_KPI {
        Long id PK
        String indicador
        Double valorActual
        Double meta
        Double porcentajeCumplimiento
        String estado
    }

    EMPLEADOS ||--o{ ATTENDANCE_RECORDS : "ficha"
    EMPLEADOS ||--o| WORK_SCHEDULES : "tiene asignado"
    EMPLEADOS ||--o{ REGISTROS_HORARIOS : "carga"
    EMPLEADOS ||--o{ OBJETIVOS : "tiene como meta"
    EMPLEADOS ||--o{ PRODUCTIVIDAD_DIARIA : "genera"
    EMPLEADOS ||--o{ ASISTENCIA_DIARIA : "genera"
```

`RESUMEN_KPI` queda fuera del diagrama de relaciones: es una tabla independiente (indicadores
globales cargados desde Excel), sin vínculo a `EMPLEADOS` ni a ninguna otra entidad.

## Notas sobre las relaciones

Todas las relaciones de `EMPLEADOS` hacia los módulos nuevos (`ATTENDANCE_RECORDS`,
`WORK_SCHEDULES`, `REGISTROS_HORARIOS`, `OBJETIVOS`) están modeladas **sin clave foránea real en
la base** — cada una guarda solo el `Long employeeId`/`empleadoId`, sin `@ManyToOne`/`@JoinColumn`
hacia `Empleados`. Es una decisión de diseño ya tomada en esos módulos (documentada en el propio
código): evita acoplar el esquema de cada módulo al de Empleados y no exige una consulta a otro
agregado para validar el dato. La contrapartida es que la integridad referencial (que ese
`employeeId` exista de verdad) no la garantiza la base de datos, sino el código de la aplicación.

En cambio, `PRODUCTIVIDAD_DIARIA` y `ASISTENCIA_DIARIA` — ambas preexistentes al login/roles,
parte del flujo de migración desde Excel — sí tienen una relación JPA real (`@ManyToOne` con
`@JoinColumn(empleado_id)`), con la restricción `FOREIGN KEY` correspondiente en la base.

## Cardinalidades

| Relación | Cardinalidad | Motivo |
|---|---|---|
| Empleados → AttendanceRecord | 1 a N | Un empleado puede tener muchos fichajes (uno por cada entrada/salida). |
| Empleados → WorkSchedule | 1 a 0..1 | Un empleado tiene, a lo sumo, un horario vigente a la vez (`employeeId` único). |
| Empleados → RegistroHorario | 1 a N | Un empleado carga muchos registros horarios (uno por hora, como máximo). |
| Empleados → Objetivo | 1 a N | Un empleado puede tener varios objetivos (distintos tipos y/o semanas). |
| Empleados → ProductividadDiaria | 1 a N | Un empleado tiene un registro de productividad por día. |
| Empleados → AsistenciaDiaria | 1 a N | Un empleado tiene un resumen de asistencia por día (importado de Excel). |

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
        bytes fotoReferencia "foto de enrolamiento, verificacion facial; nunca en JSON"
    }

    ATTENDANCE_RECORDS {
        Long id PK
        Long employeeId "sin FK de base, solo el id"
        Instant clockInAt
        Instant clockOutAt
        Instant createdAt
        Instant updatedAt
        bytes fotoCapturada "solo en clock-in; servida aparte, no en el JSON"
        Double similitudFacial "0..1, null si no hubo foto"
        EstadoVerificacionFacial estadoVerificacion "VERIFICADO_AUTOMATICO, PENDIENTE_REVISION, VERIFICADO_MANUAL, RECHAZADO"
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

    OBJETIVOS {
        Long id PK
        Long empleadoId "sin FK de base"
        TipoObjetivo tipo "PEDIDOS, DINERO"
        Double valorSemanal
        LocalDate semanaInicio "lunes de la semana"
        Instant createdAt
        Instant updatedAt
    }

    REGISTRO_PRODUCTIVIDAD {
        Long id PK
        LocalDate fecha
        Integer bultosPreparados
        Integer pedidosPreparados
        Integer pedidosEncargados
        LocalDateTime fechaCarga
        Long attendance_id FK "FK real (@ManyToOne), no nulo — el fichaje de Entrada abierto al momento de cargar"
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
    EMPLEADOS ||--o{ OBJETIVOS : "tiene como meta"
    EMPLEADOS ||--o{ REGISTRO_PRODUCTIVIDAD : "genera"
    EMPLEADOS ||--o{ ASISTENCIA_DIARIA : "genera"
    ATTENDANCE_RECORDS ||--o{ REGISTRO_PRODUCTIVIDAD : "respalda"
```

`RESUMEN_KPI` queda fuera del diagrama de relaciones: es una tabla independiente (indicadores
globales cargados desde Excel), sin vínculo a `EMPLEADOS` ni a ninguna otra entidad.

## Notas sobre las relaciones

Las relaciones de `EMPLEADOS` hacia `WORK_SCHEDULES` y `OBJETIVOS` están modeladas **sin clave
foránea real en la base** — cada una guarda solo el `Long employeeId`/`empleadoId`, sin
`@ManyToOne`/`@JoinColumn` hacia `Empleados`. Es una decisión de diseño ya tomada en esos módulos
(documentada en el propio código): evita acoplar el esquema de cada módulo al de Empleados y no
exige una consulta a otro agregado para validar el dato. La contrapartida es que la integridad
referencial (que ese `employeeId` exista de verdad) no la garantiza la base de datos, sino el
código de la aplicación.

En cambio, `REGISTRO_PRODUCTIVIDAD` y `ASISTENCIA_DIARIA` sí tienen una relación JPA real
(`@ManyToOne` con `@JoinColumn(empleado_id)`), con la restricción `FOREIGN KEY` correspondiente en
la base. `REGISTRO_PRODUCTIVIDAD` además tiene una segunda FK real, no nula, hacia
`ATTENDANCE_RECORDS` (`attendance_id`): cada carga de productividad queda atada al fichaje de
Entrada que estaba abierto en ese momento — el servicio lo resuelve solo (busca la Entrada sin
Salida del empleado autenticado); si no hay ninguna abierta, la carga falla con 409.

## Verificación facial (Empleados ⇄ AttendanceRecord)

`Empleados.fotoReferencia` (cargada al dar de alta) y `AttendanceRecord.fotoCapturada` (tomada en
cada clock-in) son las dos fotos que se comparan. La comparación en sí **no vive en esta base de
datos**: la hace un microservicio Python aparte (`face-recognition-service/`), al que el backend
le manda ambas fotos y recibe de vuelta una similitud — solo el resultado (`similitudFacial`,
`estadoVerificacion`) queda persistido acá.

## Cardinalidades

| Relación | Cardinalidad | Motivo |
|---|---|---|
| Empleados → AttendanceRecord | 1 a N | Un empleado puede tener muchos fichajes (uno por cada entrada/salida). |
| Empleados → WorkSchedule | 1 a 0..1 | Un empleado tiene, a lo sumo, un horario vigente a la vez (`employeeId` único). |
| Empleados → Objetivo | 1 a N | Un empleado puede tener varios objetivos (distintos tipos y/o semanas). |
| Empleados → RegistroProductividad | 1 a N | Un empleado carga varios registros de productividad (uno por día, típicamente). |
| AttendanceRecord → RegistroProductividad | 1 a N | Un fichaje de Entrada puede respaldar varias cargas de productividad hechas mientras estuvo abierto. |
| Empleados → AsistenciaDiaria | 1 a N | Un empleado tiene un resumen de asistencia por día (importado de Excel). |

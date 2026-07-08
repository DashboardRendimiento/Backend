# 🚀 Dashboard RRHH - Backend (Spring Boot)

API REST para gestión de recursos humanos de depósito de distribuidora de bebidas: empleados,
fichaje de asistencia con verificación facial, horarios, productividad y objetivos.

Para el detalle de uso de cada endpoint (roles, ejemplos con `curl`), ver
[`docs/MANUAL_USUARIO.md`](docs/MANUAL_USUARIO.md). Para el modelo de datos, ver
[`docs/DER.md`](docs/DER.md).

## 📋 Pre-requisitos

- **Java 21**
- **Maven 3.6+** (o usar el Maven Wrapper incluido)
- **Python 3.10+** — solo si vas a levantar `face-recognition-service/` (verificación facial del
  fichaje de Entrada; opcional, sin él las entradas quedan `PENDIENTE_REVISION`)

Verificar instalación:
```bash
java -version
mvn -version
```

## 🏗️ Estructura del Proyecto

```
Backend/
├── src/
│   ├── main/
│   │   ├── java/com/rrhh/dashboard/
│   │   │   ├── DashboardApplication.java        # Clase principal
│   │   │   ├── Auth/                           # Login, JWT
│   │   │   ├── Empleados/                      # Alta, baja, búsqueda de empleados
│   │   │   ├── Asistencia/                     # Fichaje entrada/salida + verificación facial
│   │   │   ├── Horarios/                       # Horarios asignados por empleado
│   │   │   ├── Objetivos/                      # Metas semanales por empleado
│   │   │   ├── registro_productividad/         # Carga y KPIs de productividad diaria
│   │   │   ├── Migracion/                      # Import de datos desde Excel
│   │   │   ├── security/                       # Config de Spring Security + JWT
│   │   │   └── service/                        # Carga inicial de datos desde Excel
│   │   └── resources/
│   │       ├── application.properties          # Configuración
│   │       └── data/
│   │           └── Dataset_RRHH_Deposito.xlsx  # Dataset de ejemplo
│   └── test/
├── face-recognition-service/                    # Microservicio Python (FastAPI) aparte
├── docs/
│   ├── MANUAL_USUARIO.md                        # Guía de uso de la API por rol
│   └── DER.md                                   # Diagrama de entidad-relación
├── pom.xml
└── README.md
```

## 🚀 Ejecutar la Aplicación

### Con Maven Wrapper
```bash
# Linux/Mac/Git Bash
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

### Compilar y ejecutar el JAR
```bash
./mvnw clean package
java -jar target/dashboard-1.0.0.jar
```

El servidor estará disponible en: **http://localhost:8080**

Al arrancar por primera vez (si todavía no existe ningún SUPERADMIN), el sistema crea uno con
`superadmin@example.com` / `changeme` (configurable con `SUPERADMIN_EMAIL` / `SUPERADMIN_PASSWORD`
— ver [`docs/MANUAL_USUARIO.md`](docs/MANUAL_USUARIO.md), sección 2).

### Verificación facial (opcional)

La verificación automática de la foto de Entrada depende del microservicio Python en
`face-recognition-service/` — ver su propio `README.md` para instalarlo y correrlo. Sin él,
todos los fichajes de Entrada con foto quedan `PENDIENTE_REVISION` (nunca bloquea el fichaje).

## ✅ Verificar que Funciona

```bash
curl http://localhost:8080/api/health
curl http://localhost:8080/api/info
```

## 🗄️ Base de Datos

Por defecto corre contra **H2 en memoria** (se recrea en cada arranque, `ddl-auto=update`) —
pensado para desarrollo local, no persiste entre reinicios. La consola H2 queda disponible en
`http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:Dashboard`, user `sa`, sin password).

Al arrancar, además, se carga automáticamente el Excel de `src/main/resources/data/` (empleados,
productividad y asistencia de ejemplo) — ver `ExcelDataService`.

Para producción, `pom.xml` ya incluye el driver de PostgreSQL; falta configurar
`spring.datasource.*` contra una instancia real en `application.properties` (o vía variables de
entorno) y ajustar `ddl-auto`.

## 🔧 Configuración relevante

`src/main/resources/application.properties`:

```properties
server.port=8080
app.jwt.secret=${JWT_SECRET:dev-only-secret-change-me-0123456789abcdef}
app.jwt.expiration-minutes=60
app.superadmin.email=${SUPERADMIN_EMAIL:superadmin@example.com}
app.superadmin.password=${SUPERADMIN_PASSWORD:changeme}
app.reconocimiento-facial.url=${FACE_SERVICE_URL:http://localhost:8000}
app.reconocimiento-facial.umbral-auto=${FACE_MATCH_THRESHOLD:0.75}
```

**Cambiar `JWT_SECRET` y las credenciales del SuperAdmin en cualquier ambiente que no sea
desarrollo local.**

## 🧪 Testing

```bash
./mvnw test
```

## 📡 Endpoints principales

Ver el catálogo completo (roles incluidos) en
[`docs/MANUAL_USUARIO.md`](docs/MANUAL_USUARIO.md#9-catálogo-completo-de-endpoints). Resumen:

| Módulo | Base | 
|---|---|
| Auth | `POST /api/auth/login` |
| Empleados | `/api/empleados` |
| Asistencia | `/api/attendance` |
| Horarios | `/api/work-schedules` |
| Productividad | `/api/productividad` |
| Objetivos | `/api/objetivos` |
| Migración desde Excel | `POST /api/excel/import` |

## 🌐 CORS

CORS está configurado para permitir requests desde `http://localhost:3000`, `http://localhost:8080`
y `http://localhost:5173`. Editar `config/CorsConfig.java` para agregar más orígenes.

## 📚 Tecnologías

- **Spring Boot 3.2.0** (Web, Security, Data JPA, Validation)
- **JJWT** — emisión/validación de JWT
- **H2** (dev) / **PostgreSQL** (driver incluido, a configurar para producción)
- **Apache POI** — lectura de Excel
- **Lombok**
- **FastAPI + OpenCV** (`face-recognition-service/`, Python aparte) — verificación facial

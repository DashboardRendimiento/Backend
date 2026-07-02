# 🚀 Dashboard RRHH - Backend (Spring Boot)

API REST para gestión de recursos humanos de depósito de distribuidora de bebidas.

## 📋 Pre-requisitos

- **Java 17** o superior
- **Maven 3.6+** (o usar el Maven Wrapper incluido)

Verificar instalación:
```bash
java -version
mvn -version
```

## 🏗️ Estructura del Proyecto

```
backend-rrhh/
├── src/
│   ├── main/
│   │   ├── java/com/rrhh/dashboard/
│   │   │   ├── DashboardApplication.java        # Clase principal
│   │   │   ├── config/
│   │   │   │   └── CorsConfig.java             # Configuración CORS
│   │   │   ├── service/
│   │   │   │   └── ExcelDataService.java       # Servicio de datos
│   │   │   ├── controller/
│   │   │   │   └── HealthController.java       # Health check
│   │   │   └── util/
│   │   │       └── DateUtils.java              # Utilidades
│   │   └── resources/
│   │       ├── application.properties          # Configuración
│   │       └── data/
│   │           └── Dataset_RRHH_Deposito.xlsx  # Datos
│   └── test/
├── pom.xml                                      # Dependencias Maven
└── README.md
```

## 🚀 Ejecutar la Aplicación

### Opción 1: Con Maven instalado
```bash
mvn spring-boot:run
```

### Opción 2: Con Maven Wrapper (si está incluido)
```bash
# Linux/Mac
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

### Opción 3: Compilar y ejecutar JAR
```bash
mvn clean package
java -jar target/dashboard-1.0.0.jar
```

El servidor estará disponible en: **http://localhost:8080**

## ✅ Verificar que Funciona

### 1. Health Check
```bash
curl http://localhost:8080/api/health
```

Respuesta esperada:
```json
{
  "status": "healthy",
  "service": "Dashboard RRHH API",
  "version": "1.0.0",
  "timestamp": "2026-07-02T...",
  "dataLoaded": true
}
```

### 2. Información del Sistema
```bash
curl http://localhost:8080/api/info
```

### 3. Navegador
Abre en tu navegador:
- http://localhost:8080/api/health
- http://localhost:8080/api/info

## 📊 Datos Cargados

Al iniciar, la aplicación carga automáticamente estas hojas del Excel:

- ✅ **Resumen_KPIs** (5 empleados)
- ✅ **Empleados** (5 registros)
- ✅ **Productividad_Diaria** (121 registros)
- ✅ **Asistencia_Diaria** (125 registros)
- ✅ **Seguridad** (20 registros)
- ✅ **Capacitaciones** (25 registros)

Verás mensajes como estos en los logs:
```
✅ Cargada hoja: Resumen_KPIs (5 registros)
✅ Cargada hoja: Empleados (5 registros)
...
✨ Datos cargados exitosamente
```

## 🔧 Configuración

### application.properties

```properties
# Puerto del servidor
server.port=8080

# Ubicación del archivo Excel
app.excel.file=classpath:data/Dataset_RRHH_Deposito.xlsx

# Nivel de logs
logging.level.com.rrhh.dashboard=DEBUG
```

### Cambiar Puerto
Si el puerto 8080 está ocupado:
```properties
server.port=8081
```

## 🧪 Testing

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar con cobertura
mvn test jacoco:report
```

## 🐛 Troubleshooting

### Error: "Port 8080 is already in use"
```properties
# Cambiar en application.properties:
server.port=8081
```

### Error: "Cannot find Excel file"
Verificar que el archivo esté en:
```
src/main/resources/data/Dataset_RRHH_Deposito.xlsx
```

### Error: "Java version must be 17 or higher"
```bash
# Descargar Java 17:
# https://adoptium.net/
```

### Maven no descarga dependencias
```bash
mvn clean install -U
```

## 📡 Endpoints Disponibles

### Health Check
```
GET /api/health
```

### Información del Sistema
```
GET /api/info
```

### Raíz de la API
```
GET /api
```

## 🔄 Hot Reload (Desarrollo)

Spring Boot DevTools está incluido, por lo que los cambios en el código se recargan automáticamente.

## 📦 Compilar para Producción

```bash
# Compilar sin tests
mvn clean package -DskipTests

# El JAR estará en:
target/dashboard-1.0.0.jar

# Ejecutar en producción:
java -jar target/dashboard-1.0.0.jar
```

## 🌐 CORS

CORS está configurado para permitir requests desde:
- http://localhost:3000
- http://localhost:8080
- http://localhost:5173

Editar `CorsConfig.java` para agregar más orígenes.

## 📚 Tecnologías

- **Spring Boot 3.2.0** - Framework
- **Apache POI 5.2.5** - Lectura de Excel
- **Lombok** - Reducir boilerplate
- **Maven** - Gestión de dependencias

## 📝 Notas

- Los datos se cargan en memoria al iniciar (no hay DB todavía)
- Próximos pasos: agregar controllers REST 
- Migración a PostgreSQL 
# ---- Etapa 1: build ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cachear dependencias en su propia capa (solo se re-descargan si cambia el pom.xml)
COPY pom.xml .
RUN mvn -q -B dependency:go-offline

COPY src ./src
RUN mvn -q -B clean package -DskipTests

# ---- Etapa 2: runtime ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring

COPY --from=build /app/target/*.jar app.jar

# Cloud Run inyecta $PORT (default 8080); server.port en application.properties ya lo respeta.
EXPOSE 8080

# -XX:MaxRAMPercentage en vez de -Xmx fijo: se ajusta solo al limite de memoria
# del contenedor (Cloud Run), en vez de asumir un tamano de heap fijo.
#
# -Djava.net.preferIPv4Stack=true: el pooler de Supabase (Supavisor) fallaba
# con "no tenant identifier provided" solo al conectar desde Cloud Run (nunca
# en local) -- sospecha de resolucion DNS/IPv6 en la red de salida de Cloud
# Run que Supavisor no maneja bien para identificar el tenant. Forzar IPv4
# evita esa ruta.
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-Djava.net.preferIPv4Stack=true", "-jar", "app.jar"]

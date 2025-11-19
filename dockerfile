# Etapa 1: Build
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

# Copiar archivos de wrapper + pom.xml primero (para caché eficiente)
COPY mvnw ./
COPY .mvn ./.mvn
COPY pom.xml .

# Dar permisos de ejecución a mvnw (importante en Linux)
RUN chmod +x ./mvnw

# Copiar código fuente
COPY src ./src

# Construir con caché de Maven
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw clean package -DskipTests --no-transfer-progress

# Etapa 2: Runtime
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copiar JAR con nombre genérico
COPY --from=builder /app/target/*.jar app.jar

# Exponer puerto + HEALTHCHECK (clave para reinicios rápidos)
EXPOSE 8080
HEALTHCHECK --interval=20s --timeout=5s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/api/health || exit 1

# Ejecutar (con flags de JVM para desarrollo)
ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "app.jar"]
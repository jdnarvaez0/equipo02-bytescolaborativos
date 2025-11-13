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

# Copiar el JAR generado desde la etapa de build
COPY --from=builder /app/target/recommender-engine-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto de la aplicación
EXPOSE 8080

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
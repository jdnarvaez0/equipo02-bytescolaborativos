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

# Construir
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw clean package -DskipTests --no-transfer-progress

# Etapa 2: Runtime
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
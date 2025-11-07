# CodeBytes (Equipo #2) - Recommender Engine

Motor de recomendaciones para torneos y productos.

##  Para ejecutar el proyecto:

```bash
docker-compose up --build
```
## Endpoint y enlaces
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health check: http://localhost:8080/api/health

## Credenciales de desarrollo
* Usuario: admin
* Contraseña: password
* Roles: ADMIN, PLAYER

## Tecnologías
* Spring Boot 3.5.7
* Java 17
* PostgreSQL
* Docker
* Swagger/OpenAPI

## Estructura (sugerida) del proyecto
```
src/main/java/com/codebytes2/recommender
├── config     → Configuración (Swagger, Security)
│   ├── security
│   └── swagger
├── controller → Endpoints API REST
├── dto        → Data Transfer Object (request/response)
│   ├── request
│   └── response
├── model      → Entidades JPA
├── repository → Repositorios
├── service    → Lógica de negocio
│   └── impl
├── mapper     → Mapeo DTO ↔ Entity (MapStruct)
├── exception  → Excepciones personalizadas
└── ... 
```
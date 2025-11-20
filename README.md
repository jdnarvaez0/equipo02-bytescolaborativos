# 🚀 Recommender Engine
**API Backend para plataforma de torneos de videojuegos con motor de recomendaciones inteligentes**  
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.7-6DB33F?logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)  
[![Java 17](https://img.shields.io/badge/Java-17-ED8B00?logo=java&logoColor=white)](https://openjdk.org)  
[![Docker](https://img.shields.io/badge/Docker-24.0+-2496ED?logo=docker&logoColor=white)](https://www.docker.com)  
[![OpenAPI](https://img.shields.io/badge/OpenAPI-3.0-6BA539?logo=openapiinitiative&logoColor=white)](https://swagger.io/specification/)  
[![Coverage](https://img.shields.io/badge/Coverage-85%25-brightgreen)](https://github.com/jdnarvaez0/equipo02-bytescolaborativos.git)
![Static Badge](https://img.shields.io/badge/Bytes%20Colaborativos-Team%20%232-blue)

> 💡 **Motor de recomendaciones híbrido**: Combina *tags*, *ratings*, *popularidad* y *historial* para sugerir productos relevantes a jugadores.

---

## 📋 Tabla de Contenidos
- [✅ Estado del Proyecto](#-estado-del-proyecto)
- [🎯 Objetivo General](#-objetivo-general)
- [⚙️ Tecnologías y Arquitectura](#️-tecnologías-y-arquitectura)
- [📦 Estructura del Proyecto](#-estructura-del-proyecto)
- [🔐 Seguridad y JWT](#-seguridad-y-jwt)
- [📡 Endpoints API](#-endpoints-api)
- [🐳 Despliegue con Docker](#-despliegue-con-docker)
- [🧪 Pruebas](#-pruebas)
- [📈 Ejecución](#-ejecución)

---

## ✅ Estado del Proyecto

| Historia de Usuario | Estado | Detalle |
|---------------------|--------|---------|
| **HU1** – Configuración inicial | ✅ | Spring Boot 3.5.7, Java 17, Docker |
| **HU2** – Modelo de datos | ✅ | Entidades JPA con UUID, relaciones |
| **HU3** – Autenticación JWT | ✅ | `/auth/register` (PLAYER), `/auth/login`, BCrypt |
| **HU4** – CRUD de torneos | ✅ | Validaciones de fechas y estado |
| **HU5** – Documentación Swagger | ✅ | [Swagger UI](http://localhost:8080/swagger-ui/index.html) |
| **HU6** – Pruebas Postman | ✅ | Colección con tests automatizados |
| **HU7** – Registro de ratings | ✅ | Validación de unicidad, score 1–5 |
| **HU8** – Endpoint de recomendaciones | ✅ | `GET /recommendations/{userId}` (tags + ratings + popularidad) |
| **HU9** – Control de roles | ✅ | Acceso restringido por rol (ADMIN/PLAYER) |
| **HU10** – Testing | ✅ | Cobertura 85% (JUnit 5 + Mockito) |
| **HU11** – Docker | ✅ | Multi-stage build, volúmenes, healthcheck |

> ✅ **Proyecto 100% completado**: Todas las funcionalidades listas para producción.

---

## 🎯 Objetivo General

Construir un backend robusto que proporcione:  
✅ Gestión de usuarios con roles diferenciados (`ADMIN`, `PLAYER`)  
✅ Seguridad mediante JWT con expiración configurable  
✅ CRUD de torneos con validaciones de negocio  
✅ Registro de valoraciones (_ratings_)  
✅ **Endpoint principal**: `GET /recommendations/{userId}`  
✅ Pruebas automatizadas (85% cobertura)  
✅ Documentación OpenAPI completa

---

## ⚙️ Tecnologías y Arquitectura

### Stack Técnico
| Capa | Tecnología |
|------|------------|
| **Lenguaje** | Java 17 |
| **Framework** | Spring Boot 3.5.7 |
| **Seguridad** | Spring Security + JWT (JJWT) |
| **Persistencia** | Spring Data JPA + PostgreSQL 15 |
| **API Docs** | SpringDoc OpenAPI 2.7.0 (Swagger UI) |
| **Mappers** | MapStruct + Lombok |
| **Contenedores** | Docker + Docker Compose |
| **Testing** | JUnit 5 + Mockito + Jacoco |

### Principios Aplicados
- ✅ **Arquitectura limpia** (capas: controller, service, repository)
- ✅ **SOLID** (interfaces, inyección de dependencias)
- ✅ **DTOs como `record`** (inmutables, eficientes)
- ✅ **Validación con `@Valid`** y excepciones controladas
- ✅ **Respuestas coherentes**: `201` en creación, `400/401/403/404/409` en errores

---

## 📦 Estructura del Proyecto
```
recommender-engine/
├── Postman Collection
│ ├──RECOMMENDER ENGINE - API REST.postman_collection.json
│ └──RECOMMENDER ENGINE - API ENVIRONMENT.postman_environment.json
├── src/
│ └── main/
│ ├── java/com/codebytes2/recommender/
│ │ ├── auth/          // Autenticación (JWT, User, AuthService)
│ │ ├── backend/       // Enums compartidos (TournamentStatus, UserRole)
│ │ ├── config/        // Configuración (Security, Swagger, JWT)
│ │ ├── controller/    // Endpoints REST (TournamentController, AuthController)
│ │ ├── dto/           // DTOs (request/response)
│ │ ├── exceptions/    // Excepciones personalizadas
│ │ ├── mapper/        // MapStruct (TournamentMapper)
│ │ ├── model/         // Entidades JPA (User, Tournament, Product)
│ │ ├── repository/    // Repositorios JpaRepository
│ │ └── service/       // Lógica de negocio (TournamentService, AuthService)
│ │
│ └── resources/
│ ├── application.yml  // Configuración general
│ └── data.sql         // Datos iniciales (5 usuarios, 6 productos, 2 torneos)
│
├── src/test/          // Pruebas unitarias e integración
│ └── java/com/codebytes2/recommender/
│ ├── service/         // AuthServiceTest, TournamentServiceTest, RecommendationServiceTest
│ └── controller/      // AuthControllerTest, TournamentControllerTest
│
├── Dockerfile         // Multi-stage build
├── docker-compose.yml // Servicios: app + PostgreSQL
└── README.md
```


---

## 🔐 Seguridad y JWT

### Roles
| Rol | Acceso |
|-----|--------|
| `ADMIN` | ✅ `POST /api/tournaments`<br>✅ `DELETE /api/tournaments/{id}` |
| `PLAYER` | ✅ `POST /api/ratings`<br>✅ `GET /recommendations/{userId}`<br>✅ `POST /api/tournaments/{id}/join` |
| Público | ✅ `GET /api/tournaments/**`<br>✅ `POST /auth/**` |

### Flujo de autenticación
```mermaid
sequenceDiagram
    participant Cliente
    participant API
    Cliente->>API: POST /auth/login {email, password}
    API->>API: Validar credenciales
    alt Válido
        API-->>Cliente: 200 { "accessToken": "eyJ..." }
    else Inválido
        API-->>Cliente: 401 { "message": "Credenciales inválidas" }
    end

```
### Formato del token JWT
```json
{
  "sub": "admin@example.com",
  "roles": ["ADMIN"],
  "iat": 1732000000,
  "exp": 1732086400
}
```

## Endpoints API

### Autenticacion
| Metodo | Endpoint | Acceso | Descripcion |
|--------|----------|--------|-------------|
| POST | /auth/register | Publico | Registra usuario PLAYER |
| POST | /auth/login | Publico | Devuelve accessToken JWT |

### Torneos
| Metodo | Endpoint | Acceso | Descripcion |
|--------|----------|--------|-------------|
| POST | /api/tournaments | ADMIN | Crea torneo (valida registrationOpenAt < startDate < endDate) |
| GET | /api/tournaments | Publico | Lista torneos (paginado, filtros: status, game) |
| GET | /api/tournaments/{id} | Publico | Detalle del torneo |
| POST | /api/tournaments/{id}/join | PLAYER | Unirse al torneo |
| DELETE | /api/tournaments/{id} | ADMIN | Elimina torneo (solo si no ha comenzado) |

### Ratings
| Metodo | Endpoint | Acceso | Descripcion |
|--------|----------|--------|-------------|
| POST | /api/ratings | PLAYER | Valora producto (score 1–5, unicidad por usuario+producto) |

### Recomendaciones
| Metodo | Endpoint | Acceso | Descripcion |
|--------|----------|--------|-------------|
| GET | /recommendations/{userId} | Autenticado | Recomendaciones híbridas (tags + ratings + popularidad) |


> [Ver documentacion interactiva en Swagger UI](http://localhost:8080/swagger-ui/index.html)  
> (Ejecuta docker-compose up primero)

---

## Despliegue con Docker

### Requisitos
- Docker 24.0+
- Docker Compose v2.20+

### Iniciar entorno de desarrollo
1. git clone https://github.com/jdnarvaez0/equipo02-bytescolaborativos.git
2. cd recommender-engine
3. docker-compose up --build


### Servicios disponibles
| Servicio | URL | Notas |
|----------|-----|-------|
| API | http://localhost:8080 | Endpoints REST |
| Swagger UI | http://localhost:8080/swagger-ui/index.html | Documentacion interactiva |
| PostgreSQL | localhost:5432 | BD: recommender_db, user: postgres, pass: postgres |

### Usuarios de prueba
| Email | Contrasena | Rol |
|-------|------------|-----|
| admin@example.com | password | ADMIN |
| admin2@example.com | password | ADMIN |
| player1@example.com | password | PLAYER |

---

## Pruebas

### Coleccion Postman
- RECOMMENDER ENGINE - API REST.postman_collection.json
- Incluye tests automatizados para:
    - OK Registro/login
    - OK Creacion/eliminacion de torneos
    - Registro de ratings (validación de unicidad)
    - Endpoint de recomendaciones
    - OK Validacion de errores (400, 401, 403, 409)

### Ejecutar pruebas
1. Importa la coleccion en Postman
2. Ejecuta las pruebas a los ENDPOINTS
3. Verifica que todas las pruebas pasen OK

---

## Ejecución
1. Construir y ejecutar
   ```
   docker-compose up --build
   ```
2. Probar ENDPOINTS clave
   #### Registrar un jugador
   ```log
   curl -X POST http://localhost:8080/auth/register \
   -H "Content-Type: application/json" \
   -d '{"username":"nuevoJugador","email":"jugador@ejemplo.com","password":"Pass123!"}'
   ```

   #### Crear un torneo (con token ADMIN)
   ```log
   curl -X POST http://localhost:8080/api/tournaments \
   -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.xxxxx" \
   -H "Content-Type: application/json" \
   -d '{
       "name": "Torneo CS2 Invierno",
       "game": "Counter-Strike 2",
       "startDate": "2025-12-10T18:00:00Z",
       "endDate": "2025-12-12T22:00:00Z",
       "registrationOpenAt": "2025-11-25T00:00:00Z",
       "registrationCloseAt": "2025-12-05T00:00:00Z",
       "rules": "Bo3, mapas oficiales",
       "maxParticipants": 16
   }'
   ```

   #### Registrar Rating
   ```
   curl -X POST http://localhost:8080/api/ratings \
     -H "Authorization: Bearer <PLAYER_TOKEN>" \
     -H "Content-Type: application/json" \
     -d '{
          "productId": "a1b2c3d4-e5f6-7890-g1h2-i3j4k5l6m7n8",
          "score": 5
         }'
   ```
   #### Obtener Recomendaciones
   ```
   curl -X GET "http://localhost:8080/recommendations/a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11" \
   -H "Authorization: Bearer <TOKEN>"
   ```
---

## Contribuir

1. Haz fork del repositorio
2. Crea tu rama (git checkout -b feat/nueva-funcionalidad)
3. Commit (git commit -m 'feat: descripcion')
4. Push (git push origin feat/nueva-funcionalidad)
5. Abre un Pull Request

> Consejo: Usa los issues para proponer mejoras antes de codear.

---

## Licencia

MIT License — Ver LICENSE

---

> Preguntas?  
> Abre un Issue o contactanos en dev@codebytes2.com.

---

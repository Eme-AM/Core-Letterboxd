# Letterboxd Core - Event Hub

**Event-Driven Architecture Hub for Movie Social Platform**

## Descripción

Este es el **módulo Core** del sistema Letterboxd, funcionando como un Hub de Mensajería centralizado que gestiona la comunicación asincrónica entre todos los módulos del sistema.

## Arquitectura del Sistema

### Módulos Independientes (Desarrollados por equipos externos)
- **Movies**: Gestión de películas y metadatos
- **Users**: Gestión de usuarios, autenticación y perfiles  
- **Reviews & Ratings**: Sistema de reseñas y calificaciones
- **Social Graph & Activity Feed**: Relaciones sociales y feed de actividades
- **Discovery & Recommendations**: Búsquedas y recomendaciones personalizadas
- **Analytics & Insights**: Dashboards y analíticas

### 🎬 Letterboxd Core - Event Hub

## Descripción

El **Letterboxd Core** es el hub central de mensajería para la arquitectura orientada a eventos de la plataforma Letterboxd. Este módulo se encarga de recibir, validar, enrutar y reenviar eventos entre todos los módulos del sistema de manera asíncrona.

## Funcionalidades Principales

### 🔄 Gestión de Eventos (Asíncrona)
- **Recepción de eventos** de otros módulos
- **Enrutamiento inteligente** a módulos específicos o broadcast
- **Sistema de reintentos** automático con Dead Letter Queue
- **Persistencia** de eventos para auditoría y monitoreo

### 📊 APIs REST (Síncronas)
- **Dashboard web** para visualización de eventos
- **APIs de búsqueda y filtrado** de eventos
- **Gestión de reintentos manuales**
- **Estadísticas** en tiempo real
- **Health checks** del sistema

## Arquitectura

```
┌─────────────────────────────────────────────────────────────┐
│                    LETTERBOXD CORE HUB                      │
├─────────────────────────────────────────────────────────────┤
│  🌐 REST APIs          │  🔄 Event Processing               │
│  ├─ Dashboard          │  ├─ RabbitMQ Integration           │
│  ├─ Event Search       │  ├─ Event Routing                  │
│  ├─ Statistics         │  ├─ Retry Mechanism                │
│  └─ Health Checks      │  └─ Dead Letter Queue              │
├─────────────────────────────────────────────────────────────┤
│  💾 Database (MySQL)   │  📊 Monitoring & Analytics         │
│  ├─ Event Messages     │  ├─ Real-time Stats                │
│  ├─ Retry Tracking     │  ├─ Performance Metrics            │
│  └─ Error Logs         │  └─ Health Monitoring              │
└─────────────────────────────────────────────────────────────┘
```

## Configuración y Uso

### Prerrequisitos
- Java 17+
- MySQL 8.0+
- RabbitMQ 3.8+
- Maven 3.6+

### APIs Disponibles
- `POST /api/core/events` - Publicar evento
- `GET /api/core/events` - Buscar eventos con filtros
- `GET /api/core/events/{id}` - Obtener evento específico
- `POST /api/core/events/{id}/retry` - Reintentar evento fallido
- `GET /api/core/events/stats` - Estadísticas de eventos
- `GET /api/core/health` - Estado de salud del sistema
- `GET /dashboard` - Dashboard web de monitoreo

### Tipos de Eventos Soportados
- **Usuario**: `USER_REGISTERED`, `USER_UPDATED`, `USER_DELETED`
- **Películas**: `MOVIE_CREATED`, `MOVIE_UPDATED`, `MOVIE_DELETED`
- **Reviews**: `REVIEW_POSTED`, `REVIEW_UPDATED`, `REVIEW_DELETED`
- **Social**: `USER_FOLLOWED`, `USER_UNFOLLOWED`, `LIST_SHARED`
- **Discovery**: `SEARCH_PERFORMED`, `RECOMMENDATION_GENERATED`
- **Analytics**: `ANALYTICS_UPDATED`

### Módulos Conectados
- `usuarios` - Gestión de usuarios
- `peliculas` - Catálogo de películas  
- `reviews` - Reseñas y calificaciones
- `social` - Red social y feeds
- `discovery` - Búsqueda y recomendaciones
- `analytics` - Análisis y métricas

**Desarrollado para el curso Desarrollo de Aplicaciones II - UADE**
**Responsabilidades del Event Hub:**
-  Recibir eventos del resto de los módulos
-  Enrutar eventos a los consumidores correspondientes  
-  Gestionar reintentos automáticos
-  Contar con un frontend que permita visualizar los mensajes
-  Opciones de búsqueda y filtrado de eventos
-  Monitoreo y estadísticas en tiempo real

## Tecnologías

- **Java 17**
- **Spring Boot 3.3.2**
- **Spring Security** (JWT Authentication)
- **Spring Data JPA**
- **MySQL** Database
- **Lombok**
- **Swagger/OpenAPI** (Documentación de APIs)
- **Maven**

## Base de Datos

### Entidades Principales

**EventMessage**
- Gestión de eventos entre módulos
- Estados: PENDING, PROCESSING, DELIVERED, FAILED, DEAD_LETTER
- Sistema de reintentos configurable
- Metadatos de correlación y prioridad

**User** 
- Usuarios del sistema con roles (ADMIN, USER)
- Perfiles completos (bio, país, website, etc.)
- Autenticación JWT integrada

**Rating**
- Sistema de calificaciones genérico
- Comentarios asociados  
- Referencias flexibles a cualquier entidad

## Configuración

### Database Setup
```bash
CREATE DATABASE letterboxd_core;
```

### Application Properties
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/letterboxd_core
spring.datasource.username=root
spring.datasource.password=root
```

## Ejecución

```bash
# Clonar el proyecto
cd letterboxd-core

# Ejecutar
./mvnw spring-boot:run
```

**URLs importantes:**
- **API Base**: http://localhost:4002
- **API Documentation**: http://localhost:4002/swagger-ui.html
- **Health Check**: http://localhost:4002/actuator/health

## APIs del Event Hub

### Publicar Evento
```http
POST /api/events/publish
Content-Type: application/json

{
  "eventType": "MOVIE_CREATED",
  "sourceModule": "MOVIES",
  "targetModule": "DISCOVERY",
  "payload": "{\"movieId\":123,\"title\":\"Inception\"}",
  "priority": 1
}
```

### Filtrar Eventos
```http
POST /api/events/filter
Content-Type: application/json

{
  "eventType": "REVIEW_ADDED",
  "status": "DELIVERED",
  "fromDate": "2024-01-01 00:00:00",
  "toDate": "2024-12-31 23:59:59",
  "page": 0,
  "size": 20
}
```

### Estadísticas
```http
GET /api/events/stats
Authorization: Bearer <JWT_TOKEN>
```

## Autenticación

El sistema usa **JWT Authentication**:

**Usuario Admin por defecto:**
- Email: `admin@letterboxd.com`
- Password: `admin123`

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@letterboxd.com",
  "password": "admin123"
}
```

## 🏃‍♂️ Testing

```bash
# Ejecutar tests
./mvnw test

# Ejecutar con cobertura
./mvnw test jacoco:report
```

## Próximos Pasos

1. **Integración con Message Queue** (RabbitMQ/Kafka)
2. **Dashboard Web** para monitoreo visual
3. **Métricas avanzadas** con Prometheus
4. **Health Checks** de módulos externos
5. **Rate Limiting** para prevenir spam
6. **Dead Letter Queue** handling

## Desarrollo

### Estructura del Proyecto
```
src/main/java/com/uade/tpo/demo/
├── controllers/          # REST Controllers
├── models/              # Entidades, DTOs, Requests
├── repository/          # Data Access Layer  
├── service/            # Business Logic
├── config/             # Configuraciones
└── exceptions/         # Exception Handling
```

### Patrones Implementados
- **Arquitectura en 3 capas** (Controller → Service → Repository)
- **Dependency Injection** (Spring IoC)
- **Builder Pattern** (Lombok)
- **Repository Pattern** (Spring Data JPA)
- **DTO Pattern** (Request/Response objects)

---

## 🌐 Infraestructura como Código

El repositorio con los scripts de Terraform para desplegar Letterboxd Core está disponible en:

🔗 [Terraform-core-letterboxd](https://github.com/joaquinoto/Terraform-core-letterboxd)


**Letterboxd Core está listo para coordinar tu plataforma de películas social!**

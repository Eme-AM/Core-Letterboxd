# Letterboxd Core - RabbitMQ Integration Guide

## 🚀 Quick Start

### 1. Prerequisites
- Java 17+
- Maven 3.6+
- Docker Desktop
- PowerShell (Windows)

### 2. Setup and Run

```powershell
# 1. Start infrastructure (RabbitMQ + MySQL)
.\scripts\start-infrastructure.ps1

# 2. Build the application
.\scripts\build-app.ps1

# 3. Run the application
.\scripts\run-app.ps1

# 4. Check health
.\scripts\health-check.ps1
```

## 📋 What's Implemented

### RabbitMQ Configuration
- ✅ Complete RabbitMQ setup with exchanges, queues, and bindings
- ✅ Dead Letter Queue (DLX) configuration for failed messages
- ✅ Message retry mechanism with exponential backoff
- ✅ JSON message serialization/deserialization
- ✅ Manual acknowledgment for reliable message processing

### Event System
- ✅ Base event class with polymorphic support
- ✅ Specific event types for each module:
  - `MovieEvent` - Movie catalog operations
  - `UserEvent` - User management and authentication
  - `ReviewEvent` - Reviews and ratings
  - `SocialEvent` - Social interactions
  - `DiscoveryEvent` - Search and recommendations
  - `AnalyticsEvent` - Metrics and insights

### Core Services
- ✅ `EventHubService` - Central event publishing
- ✅ `EventListenerService` - Event consumption and processing
- ✅ Database integration for event tracking
- ✅ Comprehensive error handling and logging

### Testing & Monitoring
- ✅ REST endpoints for testing event publishing
- ✅ Health check scripts
- ✅ RabbitMQ Management UI integration
- ✅ Swagger documentation

## 🎯 Event Flow Architecture

```
[Module] → [Core EventHub] → [RabbitMQ] → [Target Modules]
    ↓            ↓              ↓             ↓
Database    Event Store    Queue/Exchange   Processing
```

### Event Routing
- **Movies**: `movies.*` → `movies.events` queue
- **Users**: `users.*` → `users.events` queue  
- **Reviews**: `reviews.*` → `reviews.events` queue
- **Social**: `social.*` → `social.events` queue
- **Discovery**: `discovery.*` → `discovery.events` queue
- **Analytics**: `analytics.*` → `analytics.events` queue

## 🧪 Testing the Implementation

### 1. Test Movie Event
```bash
curl -X POST "http://localhost:8080/api/events/test/movie" \
  -d "action=CREATED&movieId=123&title=Inception"
```

### 2. Test User Event
```bash
curl -X POST "http://localhost:8080/api/events/test/user" \
  -d "action=REGISTERED&userId=456&email=user@example.com"
```

### 3. Test Review Event
```bash
curl -X POST "http://localhost:8080/api/events/test/review" \
  -d "action=CREATED&reviewId=789&movieId=123&rating=4.5"
```

### 4. Monitor in RabbitMQ
- Open http://localhost:15672 (guest/guest)
- Go to "Queues" tab
- See messages being processed in real-time

## 📊 Monitoring & Debugging

### Available Endpoints
- **Health Check**: `http://localhost:8080/api/actuator/health`
- **Event Testing**: `http://localhost:8080/api/events/test/health`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **RabbitMQ Management**: `http://localhost:15672`

### Useful Commands
```powershell
# Check all service health
.\scripts\health-check.ps1

# View logs
docker logs letterboxd-rabbitmq
docker logs letterboxd-mysql

# Restart services
.\scripts\stop-infrastructure.ps1
.\scripts\start-infrastructure.ps1
```

## 🔧 Configuration

### RabbitMQ Settings (application.yml)
```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
    publisher-confirms: true
    publisher-returns: true
    listener:
      simple:
        acknowledge-mode: manual
        retry:
          enabled: true
          max-attempts: 3
```

### Custom Event Hub Settings
```yaml
letterboxd:
  event-hub:
    retry:
      max-attempts: 3
      delay: 1000
    routing:
      default-exchange: letterboxd.events
      dead-letter-exchange: letterboxd.dlx
```

## 🔗 Integration with Other Modules

### For Movies Module
```java
// Example: Publishing a movie creation event
MovieEvent event = new MovieEvent("CREATED", movieId, movieData);
event.setUserId(currentUserId);
eventHubService.publishEvent(event);
```

### For Users Module
```java
// Example: Publishing user registration event
UserEvent event = new UserEvent("REGISTERED", userId, userData);
eventHubService.publishEvent(event);
```

### For Reviews Module
```java
// Example: Publishing review creation event
ReviewEvent event = new ReviewEvent("CREATED", reviewId, movieId, reviewData);
event.setUserId(reviewerId);
event.setRating(rating);
eventHubService.publishEvent(event);
```

## 🚨 Error Handling

### Retry Mechanism
- **Automatic Retry**: 3 attempts with exponential backoff
- **Dead Letter Queue**: Failed messages after max retries
- **Manual Reprocessing**: Admin can replay failed messages

### Monitoring Failed Events
1. Check RabbitMQ Management UI
2. Look at `*.failed` queues
3. Check application logs
4. Review database event_messages table

## 🔄 DevOps Best Practices Implemented

### Infrastructure as Code
- ✅ Docker Compose for local development
- ✅ RabbitMQ definitions as JSON
- ✅ Automated scripts for common tasks

### Monitoring & Observability
- ✅ Health checks for all services
- ✅ Structured logging with correlation IDs
- ✅ Event tracking in database
- ✅ RabbitMQ management interface

### Development Workflow
- ✅ Automated build scripts
- ✅ Environment-specific configurations
- ✅ Easy local setup and teardown
- ✅ Testing endpoints for validation

### Reliability
- ✅ Message persistence
- ✅ Dead letter queue handling
- ✅ Connection retry logic
- ✅ Graceful error handling

## 📚 Next Steps for DevOps

1. **CI/CD Pipeline**: Set up GitHub Actions for automated testing and deployment
2. **Kubernetes**: Create K8s manifests for production deployment
3. **Monitoring**: Integrate Prometheus + Grafana for metrics
4. **Security**: Implement proper authentication for RabbitMQ
5. **Scaling**: Configure RabbitMQ clustering for high availability

## 🆘 Troubleshooting

### Common Issues

**RabbitMQ not starting**
```powershell
docker logs letterboxd-rabbitmq
# Check if port 5672 is available
netstat -an | findstr 5672
```

**Application can't connect to RabbitMQ**
```powershell
# Verify RabbitMQ is running
.\scripts\health-check.ps1
# Check application.yml configuration
```

**Events not being processed**
- Check RabbitMQ management UI for queue sizes
- Verify listeners are registered
- Check application logs for errors

**Database connection issues**
```powershell
# Check MySQL status
docker logs letterboxd-mysql
# Test connection
docker exec -it letterboxd-mysql mysql -u root -p
```

---

¡Perfecto! Ahora tienes un sistema completo de RabbitMQ integrado en tu Core de Letterboxd. 🎉

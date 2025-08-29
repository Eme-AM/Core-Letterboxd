# Letterboxd Core - DevOps Scripts

## Quick Start

### 1. Start Infrastructure
```powershell
.\scripts\start-infrastructure.ps1
```

### 2. Build Application
```powershell
.\scripts\build-app.ps1
```

### 3. Run Application
```powershell
.\scripts\run-app.ps1
```

### 4. Run Tests
```powershell
.\scripts\run-tests.ps1
```

### 5. Stop Everything
```powershell
.\scripts\stop-all.ps1
```

## Available Scripts

| Script | Description |
|--------|-------------|
| `start-infrastructure.ps1` | Starts RabbitMQ and MySQL using Docker |
| `stop-infrastructure.ps1` | Stops and removes infrastructure containers |
| `build-app.ps1` | Builds the Spring Boot application |
| `run-app.ps1` | Runs the application with proper profiles |
| `run-tests.ps1` | Runs unit and integration tests |
| `deploy-docker.ps1` | Builds and runs the app in Docker |
| `health-check.ps1` | Checks health of all services |
| `logs.ps1` | Shows logs from all services |
| `clean.ps1` | Cleans build artifacts and Docker volumes |

## Environment Variables

Create a `.env` file in the root directory with:

```bash
# Database
DB_HOST=localhost
DB_PORT=3306
DB_NAME=letterboxd_core
DB_USERNAME=root
DB_PASSWORD=root

# RabbitMQ
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest

# JWT
JWT_SECRET=your-super-secret-jwt-key-here

# Mail
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

## Monitoring

- **RabbitMQ Management UI**: http://localhost:15672 (guest/guest)
- **Application Health**: http://localhost:8080/api/actuator/health
- **Swagger UI**: http://localhost:8080/swagger-ui.html

## Troubleshooting

### RabbitMQ Issues
```powershell
# Check RabbitMQ status
docker logs letterboxd-rabbitmq

# Restart RabbitMQ
docker restart letterboxd-rabbitmq
```

### Database Issues
```powershell
# Check MySQL status
docker logs letterboxd-mysql

# Connect to MySQL
docker exec -it letterboxd-mysql mysql -u root -p
```

### Application Issues
```powershell
# Check application logs
.\scripts\logs.ps1

# Check health endpoints
curl http://localhost:8080/api/actuator/health
```

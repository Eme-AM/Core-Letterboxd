# Health check for all services
Write-Host "Checking Letterboxd Core Services Health..." -ForegroundColor Green

# Function to test endpoint
function Test-Endpoint {
    param (
        [string]$Url,
        [string]$ServiceName
    )
    
    try {
        $response = Invoke-RestMethod -Uri $Url -Method Get -TimeoutSec 5
        Write-Host "✓ $ServiceName is healthy" -ForegroundColor Green
        return $true
    } catch {
        Write-Host "✗ $ServiceName is not responding" -ForegroundColor Red
        return $false
    }
}

# Function to test port
function Test-Port {
    param (
        [string]$Host,
        [int]$Port,
        [string]$ServiceName
    )
    
    try {
        $connection = Test-NetConnection -ComputerName $Host -Port $Port -ErrorAction SilentlyContinue
        if ($connection.TcpTestSucceeded) {
            Write-Host "✓ $ServiceName port $Port is open" -ForegroundColor Green
            return $true
        } else {
            Write-Host "✗ $ServiceName port $Port is not accessible" -ForegroundColor Red
            return $false
        }
    } catch {
        Write-Host "✗ Could not test $ServiceName port $Port" -ForegroundColor Red
        return $false
    }
}

Write-Host "=== Infrastructure Health Check ===" -ForegroundColor Cyan

# Check RabbitMQ
Write-Host "`nChecking RabbitMQ..." -ForegroundColor Yellow
$rabbitPortOk = Test-Port -Host "localhost" -Port 5672 -ServiceName "RabbitMQ AMQP"
$rabbitMgmtOk = Test-Port -Host "localhost" -Port 15672 -ServiceName "RabbitMQ Management"

if ($rabbitMgmtOk) {
    try {
        $rabbitStatus = Invoke-RestMethod -Uri "http://localhost:15672/api/overview" -Method Get -Credential (New-Object PSCredential("guest", (ConvertTo-SecureString "guest" -AsPlainText -Force))) -TimeoutSec 5
        Write-Host "✓ RabbitMQ API is responding" -ForegroundColor Green
    } catch {
        Write-Host "⚠ RabbitMQ API check failed (credentials might be different)" -ForegroundColor Yellow
    }
}

# Check MySQL
Write-Host "`nChecking MySQL..." -ForegroundColor Yellow
$mysqlOk = Test-Port -Host "localhost" -Port 3306 -ServiceName "MySQL"

# Check Application
Write-Host "`nChecking Application..." -ForegroundColor Yellow
$appHealthOk = Test-Endpoint -Url "http://localhost:8080/api/actuator/health" -ServiceName "Application Health"
$appSwaggerOk = Test-Endpoint -Url "http://localhost:8080/swagger-ui.html" -ServiceName "Swagger UI"

# Check Event Test Endpoints
Write-Host "`nChecking Event Test Endpoints..." -ForegroundColor Yellow
$eventTestOk = Test-Endpoint -Url "http://localhost:8080/api/events/test/health" -ServiceName "Event Testing"

# Summary
Write-Host "`n=== Health Check Summary ===" -ForegroundColor Cyan

$services = @(
    @{ Name = "RabbitMQ AMQP"; Status = $rabbitPortOk },
    @{ Name = "RabbitMQ Management"; Status = $rabbitMgmtOk },
    @{ Name = "MySQL"; Status = $mysqlOk },
    @{ Name = "Application Health"; Status = $appHealthOk },
    @{ Name = "Swagger UI"; Status = $appSwaggerOk },
    @{ Name = "Event Testing"; Status = $eventTestOk }
)

$healthyCount = 0
foreach ($service in $services) {
    if ($service.Status) {
        $healthyCount++
        Write-Host "✓ $($service.Name)" -ForegroundColor Green
    } else {
        Write-Host "✗ $($service.Name)" -ForegroundColor Red
    }
}

Write-Host "`nOverall Health: $healthyCount/$($services.Count) services healthy" -ForegroundColor $(if ($healthyCount -eq $services.Count) { "Green" } else { "Yellow" })

if ($healthyCount -eq $services.Count) {
    Write-Host "`n🎉 All services are healthy!" -ForegroundColor Green
    Write-Host "Available URLs:" -ForegroundColor Cyan
    Write-Host "  • Application: http://localhost:8080" -ForegroundColor White
    Write-Host "  • Health Check: http://localhost:8080/api/actuator/health" -ForegroundColor White
    Write-Host "  • Swagger UI: http://localhost:8080/swagger-ui.html" -ForegroundColor White
    Write-Host "  • RabbitMQ Management: http://localhost:15672 (guest/guest)" -ForegroundColor White
    Write-Host "  • Event Testing: http://localhost:8080/api/events/test/health" -ForegroundColor White
} else {
    Write-Host "`n⚠ Some services are not healthy. Check the logs for more details." -ForegroundColor Yellow
    Write-Host "Run .\scripts\logs.ps1 to see detailed logs" -ForegroundColor Cyan
}

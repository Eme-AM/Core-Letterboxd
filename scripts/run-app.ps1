# Run the Spring Boot application
Write-Host "Starting Letterboxd Core Application..." -ForegroundColor Green

# Navigate to project root
$projectRoot = Split-Path -Parent $PSScriptRoot
$backendPath = Join-Path $projectRoot "Back End\demo\demo"

if (-not (Test-Path $backendPath)) {
    Write-Host "✗ Backend path not found: $backendPath" -ForegroundColor Red
    exit 1
}

Set-Location $backendPath

# Check if Maven wrapper exists
if (Test-Path "mvnw.cmd") {
    $mvnCommand = ".\mvnw.cmd"
} elseif (Get-Command "mvn" -ErrorAction SilentlyContinue) {
    $mvnCommand = "mvn"
} else {
    Write-Host "✗ Maven not found. Please install Maven or use the Maven wrapper." -ForegroundColor Red
    exit 1
}

# Check if infrastructure is running
Write-Host "Checking infrastructure status..." -ForegroundColor Yellow

try {
    $rabbitCheck = Test-NetConnection -ComputerName localhost -Port 5672 -ErrorAction SilentlyContinue
    $mysqlCheck = Test-NetConnection -ComputerName localhost -Port 3306 -ErrorAction SilentlyContinue
    
    if (-not $rabbitCheck.TcpTestSucceeded) {
        Write-Host "⚠ RabbitMQ is not running on port 5672. Starting infrastructure..." -ForegroundColor Yellow
        & "$projectRoot\scripts\start-infrastructure.ps1"
    } else {
        Write-Host "✓ RabbitMQ is running" -ForegroundColor Green
    }
    
    if (-not $mysqlCheck.TcpTestSucceeded) {
        Write-Host "⚠ MySQL is not running on port 3306" -ForegroundColor Yellow
    } else {
        Write-Host "✓ MySQL is running" -ForegroundColor Green
    }
    
} catch {
    Write-Host "⚠ Could not check infrastructure status" -ForegroundColor Yellow
}

# Set environment variables
$env:SPRING_PROFILES_ACTIVE = "dev"

Write-Host "Starting application with profile: dev" -ForegroundColor Yellow
Write-Host "Using Maven command: $mvnCommand" -ForegroundColor Yellow

try {
    # Start the application
    & $mvnCommand spring-boot:run
    
} catch {
    Write-Host "✗ Failed to start application: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Start infrastructure services (RabbitMQ and MySQL)
Write-Host "Starting Letterboxd Core Infrastructure..." -ForegroundColor Green

# Check if Docker is running
try {
    docker --version | Out-Null
    Write-Host "✓ Docker is available" -ForegroundColor Green
} catch {
    Write-Host "✗ Docker is not running. Please start Docker Desktop first." -ForegroundColor Red
    exit 1
}

# Navigate to project root
$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

# Check if docker-compose.yml exists
if (-not (Test-Path "docker-compose.yml")) {
    Write-Host "✗ docker-compose.yml not found in project root" -ForegroundColor Red
    exit 1
}

Write-Host "Starting infrastructure services..." -ForegroundColor Yellow

# Start services
try {
    docker-compose up -d rabbitmq mysql
    
    Write-Host "Waiting for services to be ready..." -ForegroundColor Yellow
    Start-Sleep -Seconds 10
    
    # Check RabbitMQ health
    try {
        $rabbitHealth = docker-compose exec rabbitmq rabbitmq-diagnostics ping 2>$null
        if ($rabbitHealth -like "*pong*") {
            Write-Host "✓ RabbitMQ is ready" -ForegroundColor Green
        } else {
            Write-Host "⚠ RabbitMQ is starting, please wait..." -ForegroundColor Yellow
        }
    } catch {
        Write-Host "⚠ RabbitMQ is starting, please wait..." -ForegroundColor Yellow
    }
    
    # Check MySQL health
    try {
        $mysqlHealth = docker-compose exec mysql mysqladmin ping -h localhost --silent 2>$null
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✓ MySQL is ready" -ForegroundColor Green
        } else {
            Write-Host "⚠ MySQL is starting, please wait..." -ForegroundColor Yellow
        }
    } catch {
        Write-Host "⚠ MySQL is starting, please wait..." -ForegroundColor Yellow
    }
    
    Write-Host ""
    Write-Host "Infrastructure started successfully!" -ForegroundColor Green
    Write-Host "RabbitMQ Management: http://localhost:15672 (guest/guest)" -ForegroundColor Cyan
    Write-Host "MySQL: localhost:3306 (root/root)" -ForegroundColor Cyan
    
} catch {
    Write-Host "✗ Failed to start infrastructure: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

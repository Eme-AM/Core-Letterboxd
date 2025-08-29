# Stop all infrastructure services
Write-Host "Stopping Letterboxd Core Infrastructure..." -ForegroundColor Yellow

# Navigate to project root
$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

try {
    # Stop and remove containers
    docker-compose down
    
    Write-Host "✓ Infrastructure stopped successfully!" -ForegroundColor Green
    
    # Optional: Remove volumes (uncomment if you want to clean data)
    # Write-Host "Removing volumes..." -ForegroundColor Yellow
    # docker-compose down -v
    # Write-Host "✓ Volumes removed!" -ForegroundColor Green
    
} catch {
    Write-Host "✗ Failed to stop infrastructure: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

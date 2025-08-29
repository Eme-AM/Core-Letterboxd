# Build the Spring Boot application
Write-Host "Building Letterboxd Core Application..." -ForegroundColor Green

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

Write-Host "Using Maven command: $mvnCommand" -ForegroundColor Yellow

try {
    Write-Host "Cleaning previous build..." -ForegroundColor Yellow
    & $mvnCommand clean
    
    if ($LASTEXITCODE -ne 0) {
        throw "Maven clean failed"
    }
    
    Write-Host "Compiling and packaging application..." -ForegroundColor Yellow
    & $mvnCommand compile package -DskipTests
    
    if ($LASTEXITCODE -ne 0) {
        throw "Maven build failed"
    }
    
    Write-Host "✓ Build completed successfully!" -ForegroundColor Green
    
    # Check if JAR was created
    $jarPath = "target\*.jar"
    $jarFiles = Get-ChildItem $jarPath -ErrorAction SilentlyContinue
    
    if ($jarFiles) {
        Write-Host "✓ JAR file created: $($jarFiles[0].Name)" -ForegroundColor Green
    } else {
        Write-Host "⚠ JAR file not found in target directory" -ForegroundColor Yellow
    }
    
} catch {
    Write-Host "✗ Build failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

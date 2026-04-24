# Docker Setup & Installation Script for tyspecIO-demo
# This script prepares the environment, builds the application artifacts, and starts them using Docker Compose.

# Exit on error
$ErrorActionPreference = "Stop"

Write-Host "===============================================" -ForegroundColor Cyan
Write-Host "   tyspecIO-demo: Docker Environment Setup     " -ForegroundColor Cyan
Write-Host "===============================================" -ForegroundColor Cyan

# 1. Check Prerequisites
Write-Host "`n[1/5] Checking prerequisites..." -ForegroundColor Yellow
if (-not (Get-Command "docker" -ErrorAction SilentlyContinue)) {
    Write-Error "Docker is not installed or not in PATH. Please install Docker Desktop."
}
if (-not (Get-Command "npm" -ErrorAction SilentlyContinue)) {
    Write-Error "Node.js/NPM is not installed. Please install Node.js."
}

# 2. Install Dependencies
Write-Host "`n[2/5] Installing NPM dependencies..." -ForegroundColor Yellow
npm install

# 3. Generate API Contract & Clients
Write-Host "`n[3/5] Generating API contract and clients..." -ForegroundColor Yellow
# Generate openapi.yaml from TypeSpec
npx tsp compile libs/api-contract/main.tsp
# Generate Angular services and models using Orval
npx orval

# 4. Build Application Artifacts
Write-Host "`n[4/5] Building application artifacts (Backend & Frontend)..." -ForegroundColor Yellow

# Build Backend (Java JAR)
Write-Host "Building Backend JAR..." -ForegroundColor Gray
cd apps/backend
.\mvnw.cmd clean package -DskipTests
cd ../..

# Build Frontend (Angular static files)
Write-Host "Building Frontend..." -ForegroundColor Gray
npx nx build frontend --configuration=production

# 5. Start Docker Compose
Write-Host "`n[5/5] Launching Docker Compose..." -ForegroundColor Yellow
docker compose up -d --build

Write-Host "`n===============================================" -ForegroundColor Green
Write-Host "   Successfully started!                       " -ForegroundColor Green
Write-Host "   Frontend: http://localhost:4200            " -ForegroundColor Green
Write-Host "   Backend:  http://localhost:8081            " -ForegroundColor Green
Write-Host "   MongoDB:  localhost:27017                  " -ForegroundColor Green
Write-Host "===============================================" -ForegroundColor Green
Write-Host "Use 'docker compose logs -f' to see the logs."

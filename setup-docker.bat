@echo off
SETLOCAL EnableDelayedExpansion

echo ===============================================
echo    tyspecIO-demo: Docker Environment Setup
echo ===============================================

echo.
echo [1/5] Installing NPM dependencies...
call npm install
if %ERRORLEVEL% NEQ 0 goto :error

echo.
echo [2/5] Generating API contract and clients...
call npx tsp compile libs/api-contract/main.tsp
if %ERRORLEVEL% NEQ 0 goto :error
call npx orval
if %ERRORLEVEL% NEQ 0 goto :error

echo.
echo [3/5] Building Backend JAR...
cd apps\backend
call mvnw.cmd clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    cd ..\..
    goto :error
)
cd ..\..

echo.
echo [4/5] Building Frontend...
call npx nx build frontend --configuration=production
if %ERRORLEVEL% NEQ 0 goto :error

echo.
echo [5/5] Launching Docker Compose...
call docker compose up -d --build
if %ERRORLEVEL% NEQ 0 goto :error

echo.
echo ===============================================
echo    Successfully started!
echo    Frontend: http://localhost:4200
echo    Backend:  http://localhost:8081
echo ===============================================
pause
exit /b 0

:error
echo.
echo !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
echo    An error occurred during the setup.
echo !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
pause
exit /b 1

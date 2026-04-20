@echo off
echo Starting Full-Stack Demo Application...
echo.

echo 1/2: Building Backend (mvn clean install -e)...
echo.
cd apps\backend
call mvnw.cmd clean install -e
cd ..\..

echo.
echo 2/2: Launching Applications...
echo.
echo Launching Spring Boot Backend...
start "Backend (Spring)" npx nx serve backend

echo.
echo Launching Angular Frontend...
start "Frontend (Angular)" npx nx serve frontend

echo.
echo Both applications are starting in separate windows.
echo Frontend will be available at: http://localhost:4200
echo.
pause

@echo off
rem Builds target\pdf-merger-1.0-SNAPSHOT.jar (with PDFBox included). Tests are skipped.
setlocal

set "MVN=C:\Program Files\JetBrains\IntelliJ IDEA 2026.3\plugins\maven-plugin\lib\maven3\bin\mvn.cmd"
where mvn >nul 2>nul && set "MVN=mvn"

call "%MVN%" -f "%~dp0pom.xml" clean package -DskipTests
if errorlevel 1 (
    echo Build failed.
    exit /b 1
)

echo.
echo Built: %~dp0target\pdf-merger-1.0-SNAPSHOT.jar

@echo off
rem Starts PDF Utils without a console window. Run build.bat first.
setlocal

set "JAR=%~dp0..\target\pdf-utils-1.0-SNAPSHOT.jar"
if not exist "%JAR%" (
    echo %JAR% not found. Run build.bat first.
    pause
    exit /b 1
)

start "" javaw -jar "%JAR%"

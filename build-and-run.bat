@echo off
rem Script to build and run the application

rem Default mode is JVM
set MODE=jvm
if not "%~1"=="" set MODE=%~1

rem Check if the mode is valid
if not "%MODE%"=="jvm" if not "%MODE%"=="native" if not "%MODE%"=="container" (
    echo Invalid mode: %MODE%
    echo Usage: %0 [jvm^|native^|container]
    exit /b 1
)

rem Set license path if provided
if not "%~2"=="" set PDF_LICENSE_PATH=%~2

rem Build the application
if "%MODE%"=="jvm" (
    echo Building application in JVM mode...
    call mvnw clean package
) else if "%MODE%"=="native" (
    echo Building application in native mode...
    call mvnw clean package -Pnative
) else if "%MODE%"=="container" (
    echo Building container image...
    call mvnw clean package -Pnative -Dquarkus.native.container-build=true -Dquarkus.container-image.build=true
)

rem Run the application
if "%MODE%"=="jvm" (
    echo Running application in JVM mode...
    java -jar target\quarkus-app\quarkus-run.jar
) else if "%MODE%"=="native" (
    echo Running application in native mode...
    target\pdf-convert-1.0.0-SNAPSHOT-runner
) else if "%MODE%"=="container" (
    echo Running container image...
    docker run -p 8080:8080 pdf-convert:latest
) 
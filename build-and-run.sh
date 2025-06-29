#!/bin/bash
# Script to build and run the application

# Default mode is JVM
MODE=${1:-jvm}

# Check if the mode is valid
if [[ "$MODE" != "jvm" && "$MODE" != "native" && "$MODE" != "container" ]]; then
    echo "Invalid mode: $MODE"
    echo "Usage: $0 [jvm|native|container]"
    exit 1
fi

# Set license path if provided
if [ -n "$2" ]; then
    export PDF_LICENSE_PATH=$2
fi

# Build the application
if [ "$MODE" == "jvm" ]; then
    echo "Building application in JVM mode..."
    ./mvnw clean package
elif [ "$MODE" == "native" ]; then
    echo "Building application in native mode..."
    ./mvnw clean package -Pnative
elif [ "$MODE" == "container" ]; then
    echo "Building container image..."
    ./mvnw clean package -Pnative -Dquarkus.native.container-build=true -Dquarkus.container-image.build=true
fi

# Run the application
if [ "$MODE" == "jvm" ]; then
    echo "Running application in JVM mode..."
    java -jar target/quarkus-app/quarkus-run.jar
elif [ "$MODE" == "native" ]; then
    echo "Running application in native mode..."
    ./target/pdf-convert-1.0.0-SNAPSHOT-runner
elif [ "$MODE" == "container" ]; then
    echo "Running container image..."
    docker run -p 8080:8080 pdf-convert:latest
fi 
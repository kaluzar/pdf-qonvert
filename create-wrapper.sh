#!/bin/bash
# Script to create Maven wrapper

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Maven is not installed. Please install Maven first."
    exit 1
fi

# Create Maven wrapper
mvn wrapper:wrapper -Dmaven=3.9.5

# Make the wrapper executable
chmod +x mvnw

echo "Maven wrapper created successfully." 
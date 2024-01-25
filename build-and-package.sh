#!/bin/bash

# Ensure the script stops if any command fails
set -e

# Directory variables
BACKEND_DIR="todo-app-backend"
FRONTEND_DIR="todo-app-frontend"

# Compile Java (Spring Boot) Backend
echo "Compiling Java Backend..."
cd "$BACKEND_DIR"
mvn clean package -DskipTests
cd ..

# Compile Angular Frontend
echo "Compiling Angular Frontend..."
cd "$FRONTEND_DIR"
npm install
npm run build --prod
cd ..

# Build Docker Images with Docker Compose
echo "Building Docker Images with Docker Compose..."
docker-compose build

echo "Docker images have been built."
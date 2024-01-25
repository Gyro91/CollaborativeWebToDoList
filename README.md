# Collaborative Web ToDoList App

## Overview

This application is a collaborative todo list using reactive programming, featuring a Java Spring Boot backend and an Angular frontend. The application is containerized using Docker for easy deployment.

## Prerequisites

Before you begin, ensure you have the following installed:
- Java JDK (for Spring Boot)
- Maven (for building the backend)
- Node.js and npm (for building the frontend)
- Docker and Docker Compose (for containerization and orchestration)

## Getting Started

### Directory Structure

- `todo-app-backend`: Contains the Spring Boot backend application.
- `todo-app-frontend`: Contains the Angular frontend application.

### Building and Packaging

A script named `build-and-package.sh` is provided at the root of the project to compile and package both the backend and frontend, and to build Docker images using Docker Compose.

1. **Execute the Build Script**

   Make the script executable (if it's not already):

    ```bash
    chmod +x build-and-package.sh
    ```

   Run the script:

    ```bash
    ./build-and-package.sh
    ```

   This script performs the following actions:
    - Compiles the Java backend using Maven.
    - Installs dependencies and builds the Angular frontend.
    - Builds Docker images for both frontend and backend using Docker Compose.

### Running the Application

After building and packaging, you can run the application using Docker Compose:

```bash
docker-compose up -d

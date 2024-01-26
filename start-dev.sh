#!/bin/bash

# Starting Docker Compose services
echo "Starting Docker Compose services for development..."
docker-compose -f docker-compose-dev.yml up -d
#!/bin/bash

# Stopping Docker Compose services
echo "Stopping Docker Compose services and cleaning up..."
docker-compose -f docker-compose-dev.yml down
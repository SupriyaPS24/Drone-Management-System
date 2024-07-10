#!/bin/bash

# Step 1: Build the project
echo "Building the Maven project..."
mvn clean package

# Step 2: Run the Spring Boot application
echo "Starting the Spring Boot application..."
mvn spring-boot:run

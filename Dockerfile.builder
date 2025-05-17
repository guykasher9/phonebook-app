# syntax=docker/dockerfile:1

# ---- Build Stage ----
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy all project files
COPY . .

# Build the project (skip tests for faster build, remove -DskipTests to run tests)
RUN mvn clean package -DskipTests

# ---- Output Stage ----
FROM scratch AS export-stage
COPY --from=builder /app/target/*.jar /output/ 
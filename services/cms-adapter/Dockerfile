# Multi-stage Dockerfile for Spring Boot Maven application

# Stage 1: Build the application
FROM maven:3.9.4-eclipse-temurin-21 AS builder

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml first for better layer caching
COPY mvnw mvnw.cmd pom.xml ./
COPY .mvn .mvn

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src/ ./src/

# Build the application (skip tests for faster build, enable if needed)
RUN mvn clean package -DskipTests

# Stage 2: Create runtime image
FROM eclipse-temurin:21-jre-alpine

# Set working directory
WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Environment variables
ENV SERVER_PORT=8082
ENV RABBITMQ_HOST=rabbitmq
ENV RABBITMQ_PORT=5672

# Expose the port (default Spring Boot port)
EXPOSE 8082

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

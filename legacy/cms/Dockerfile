# Simple Dockerfile for CMS Mock Service
FROM eclipse-temurin:21-jre

# Set working directory
WORKDIR /app

# Copy the jar file
COPY target/cms-*.jar app.jar

# Environment variables
ENV SERVER_PORT=5000
ENV SPRING_APPLICATION_NAME=cms-mock
ENV JAVA_OPTS=""

# Expose port
EXPOSE 5000

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
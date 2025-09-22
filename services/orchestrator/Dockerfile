# dockerfile
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml mvnw mvnw.cmd ./
COPY .mvn/ .mvn/
RUN chmod +x mvnw
RUN ./mvnw -B -q dependency:go-offline

COPY src/ ./src/
# Run full build with CXF codegen
RUN ./mvnw -B clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

ENV SERVER_PORT=8000
EXPOSE 8000
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
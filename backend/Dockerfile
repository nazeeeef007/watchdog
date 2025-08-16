# Use Eclipse Temurin (OpenJDK) Java 17 as base image
FROM eclipse-temurin:17-jdk-alpine AS build

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml first (for dependency caching)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (this layer is cached if pom.xml hasn’t changed)
RUN ./mvnw dependency:go-offline

# Copy the rest of the source
COPY src src

# Package the Spring Boot app (creates JAR inside /app/target/)
RUN ./mvnw clean package -DskipTests

# Final runtime image
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy the packaged JAR into the runtime container
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

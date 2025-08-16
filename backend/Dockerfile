# Stage 1: Build
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copy only backend Maven wrapper and .mvn
COPY backend/mvnw .
COPY backend/.mvn .mvn
RUN chmod +x mvnw

# Copy backend pom.xml and download dependencies
COPY backend/pom.xml .
RUN ./mvnw dependency:go-offline

# Copy the backend source code
COPY backend/src ./src

# Build the Spring Boot jar (skip tests for speed)
RUN ./mvnw clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy the jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port your Spring Boot app listens on
EXPOSE 8080

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]

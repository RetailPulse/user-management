# First stage: Build the Spring Boot application
FROM maven:3.9.9-eclipse-temurin-23 AS builder

# Set working directory
WORKDIR /app

# Copy the project files
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package

# Second stage: Run the built JAR in a lightweight JDK image
FROM eclipse-temurin:24-jdk-alpine AS runner

# Set working directory
WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/retailpulse-user-management-1.0.0-SNAPSHOT.jar retailpulse-user-management-1.0.0-SNAPSHOT.jar

# Expose the application port
EXPOSE 8083

# Run the application
ENTRYPOINT ["java", "-jar", "retailpulse-user-management-1.0.0-SNAPSHOT.jar"]
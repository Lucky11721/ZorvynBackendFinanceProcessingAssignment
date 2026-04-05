# Use an official lightweight Java 17 runtime as a parent image
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the packaged jar file into our container
# Note: We will compile the code before running docker-compose
COPY target/*.jar app.jar

# Expose the port the Spring Boot application runs on
EXPOSE 8085

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
# Use OpenJDK base image
FROM openjdk:17-jdk-slim

# Set working directory in the container
WORKDIR /app

# Copy the built jar file to the container
COPY target/*.jar app.jar

# Expose the port (Render uses $PORT)
EXPOSE 8080

# Command to run the jar file
CMD ["sh", "-c", "java -Dserver.port=$PORT -jar app.jar"]
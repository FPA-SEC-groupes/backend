# Stage 1: Build the Spring Boot JAR
FROM maven:3.8.5-openjdk-18 AS build

# Set working directory
WORKDIR /app

# Copy the Maven project files
COPY pom.xml .
COPY src ./src

# Build the JAR file
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM openjdk:18

# Set working directory
WORKDIR /app

# Expose the port Spring Boot is running on
EXPOSE 8082

# Copy the built JAR file from the previous stage
COPY --from=build /app/target/HelloWay.jar HelloWay.jar

# Copy the photos directory into the container
COPY photos photos

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "HelloWay.jar"]

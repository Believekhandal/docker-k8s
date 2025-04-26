# Use JDK 23 base image
FROM openjdk:17-jdk-alpine


# Expose the application port
EXPOSE 8888

# Add the Spring Boot JAR file
ARG JAR_FILE=target/dockerdemo-1.0.5.jar
ADD ${JAR_FILE} app.jar

# Command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app.jar"]

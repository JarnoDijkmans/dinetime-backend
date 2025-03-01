FROM openjdk:21-jdk-slim

WORKDIR /app

COPY build/libs/ranking-service-0.0.1-SNAPSHOT.jar app.jar

# Expose the application's port and the debugging port (5005)
EXPOSE 8080
EXPOSE 5005

# Run the application in debug mode
ENTRYPOINT ["java", "-Xdebug", "-Xrunjdwp:server=y,transport=dt_socket,address=0.0.0.0:5005,suspend=n", "-jar", "app.jar"]


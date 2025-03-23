FROM openjdk:21-jdk-slim

WORKDIR /app

COPY build/libs/ranking-service-0.0.1-SNAPSHOT.jar ./ranking-service.jar

# Expose the application's port and the debugging port (5005)
EXPOSE 5001
EXPOSE 5005

CMD ["java", "-jar", "ranking-service.jar"]


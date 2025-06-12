FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY build/libs/identity-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 80

ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "app.jar"]

FROM maven:3.8.1-openjdk-11-slim as build

WORKDIR /app

COPY trivia-discord-bot .
# RUN mvn dependency:go-offline
RUN mvn clean compile assembly:single

FROM openjdk:11-jre-slim

WORKDIR /app

COPY --from=build /app/target/*.jar ./app.jar

CMD ["java", "-jar", "/app/app.jar"]
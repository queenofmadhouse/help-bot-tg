# Используем официальный образ Maven для сборки приложения
FROM maven:3.9.4-eclipse-temurin-21 AS build
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package

# Используем базовый образ Java 8 для запуска приложения
FROM eclipse-temurin:21-jdk-alpine
COPY --from=build /usr/src/app/target/help-bot-0.0.1-SNAPSHOT.jar /usr/app/help-bot-0.0.1-SNAPSHOT.jar
CMD ["java", "-jar", "/usr/app/help-bot-0.0.1-SNAPSHOT.jar"]

# Fase 1: Construcción
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Fase 2: Imagen final
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/literalura-0.0.1-SNAPSHOT.jar literalura.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/literalura.jar"]

# Etapa 1: Construcción (Build)
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Compilamos el proyecto omitiendo los tests para mayor rapidez en el despliegue
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución (Run)
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/literalura-0.0.1-SNAPSHOT.jar literalura.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "literalura.jar"]
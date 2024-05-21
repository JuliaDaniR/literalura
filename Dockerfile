FROM openjdk:17-jdk-slim

COPY target/literalura-0.0.1-SNAPSHOT.jar literalura.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/literalura.jar"]
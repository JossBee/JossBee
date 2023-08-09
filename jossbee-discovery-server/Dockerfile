# Stage 1: Build the JAR file
FROM maven:3.6.3-openjdk-8 AS build
WORKDIR /workspace
COPY pom.xml .

RUN mvn dependency:go-offline

COPY src/ /workspace/src/

RUN mvn package -DskipTests

# Stage 2: Create a minimal image
FROM adoptopenjdk:8-jre-hotspot
WORKDIR /app
COPY --from=build /workspace/target/*.jar josbee-discovery-service.jar
EXPOSE 8761
CMD ["java", "-jar", "josbee-discovery-service.jar"]
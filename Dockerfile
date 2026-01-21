# 1. BUILD STAGE
FROM maven:3.9.5-eclipse-temurin-17 AS build
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B
COPY src src
RUN ./mvnw package -DskipTests

# 2. RUNTIME STAGE
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/matcha-0.0.1-SNAPSHOT.jar app.jar
RUN chmod -R 755 /app || true
EXPOSE 8080
ENTRYPOINT ["java", "-Xmx512m", "-jar", "app.jar"]
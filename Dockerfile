FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY backend/pom.xml .
COPY backend/src ./src
RUN mvn -q package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/biotech-tracker.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Xmx256m", "-jar", "app.jar"]

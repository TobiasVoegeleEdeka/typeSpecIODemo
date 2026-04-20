# Run stage - Fast & Lightweight
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# We copy the locally built JAR directly to avoid slow/flaky Docker builds
COPY apps/backend/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]

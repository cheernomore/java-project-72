# Этап 1: Сборка приложения
FROM eclipse-temurin:21 AS builder
WORKDIR /app

COPY . .

RUN app/gradlew clean shadowJar

# Этап 2: Создание минимального образа для запуска
FROM eclipse-temurin:21
WORKDIR /app

COPY --from=builder /app/build/libs/app-1.0-SNAPSHOT-all.jar app.jar

ENV PORT=8081
ENTRYPOINT ["java", "-jar", "app.jar"]
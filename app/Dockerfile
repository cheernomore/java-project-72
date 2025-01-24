FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradlew .

RUN ./gradlew --no-daemon dependencies

COPY src .

RUN ./gradlew --no-daemon shadowJar

ENV PORT=8080

CMD java -jar build/libs/app-1.0-SNAPSHOT-all.jar
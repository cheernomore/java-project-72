FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY app/gradle gradle
COPY app/build.gradle.kts .
COPY app/settings.gradle.kts .
COPY app/gradlew .

RUN ./gradlew --no-daemon dependencies

COPY app/src .

RUN ./gradlew --no-daemon shadowJar

ENV PORT=8080

CMD java -jar build/libs/app-1.0-SNAPSHOT-all.jar
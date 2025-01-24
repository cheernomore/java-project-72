FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY app/ ./

RUN ./gradlew --no-daemon shadowJar

ENV PORT=8080

CMD java -jar build/libs/app-1.0-SNAPSHOT-all.jar
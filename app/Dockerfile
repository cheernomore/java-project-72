FROM eclipse-temurin:21

WORKDIR app

COPY . .

RUN gradlew shadowJar

ENV PORT=8081

CMD java -jar app/build/libs/app-1.0-SNAPSHOT-all.jar
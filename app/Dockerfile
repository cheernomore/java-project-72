FROM eclipse-temurin:21

WORKDIR .

COPY . .

RUN chmod +x app/gradlew && app/gradle shadowJar

ENV PORT=8081

CMD java -jar app/build/libs/app-1.0-SNAPSHOT-all.jar
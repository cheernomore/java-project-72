FROM eclipse-temurin:21-jdk
WORKDIR app
COPY app .
RUN ./gradlew --no-daemon shadowJar
CMD java -jar build/libs/app.jar

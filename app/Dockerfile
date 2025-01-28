FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY app/ ./

RUN ./gradlew --no-daemon shadowJar

ENV PORT=8080
ENV JDBC_DATABASE_URL=jdbc:postgresql://o8RaihtbgQvdeTQKDXJreLCuEsEWyyxE@dpg-cubrnbl6l47c73a4cj10-a:5432/project_s15r
ENV JDBC_DATABASE_USERNAME=test
ENV JDBC_DATABASE_PASSWORD=o8RaihtbgQvdeTQKDXJreLCuEsEWyyxE

CMD java -jar build/libs/app-1.0-SNAPSHOT-all.jar
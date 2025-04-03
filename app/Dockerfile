FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY app/ ./


RUN ./gradlew --no-daemon shadowJar

ENV PORT=8080
ENV JDBC_DATABASE_URL=jdbc:postgresql://dpg-cvncl2uuk2gs73cs03bg-a:5432/java_project_72_tn4s
ENV JDBC_DATABASE_USERNAME=java_project_72_tn4s_user
ENV JDBC_DATABASE_PASSWORD=VkpsWJRw6DCm5GCmqK0UpqDUvMw1pHPL

CMD java -jar build/libs/app-1.0-SNAPSHOT-all.jar
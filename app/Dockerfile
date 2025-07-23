FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY app/ ./


RUN ./gradlew --no-daemon shadowJar

ENV PORT=8080
ENV JDBC_DATABASE_URL=jdbc:postgresql://dpg-d20fm3ngi27c73cims1g-a:5432/java_project_72_postgres
ENV JDBC_DATABASE_USERNAME=java_project_72_postgres_user
ENV JDBC_DATABASE_PASSWORD=DzY0QsYQ6UjNNhsMlnRh7sxxR9KJmJty

CMD java -jar build/libs/app-1.0-SNAPSHOT-all.jar
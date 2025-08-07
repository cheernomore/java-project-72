plugins {
    application
    jacoco
    id("io.freefair.lombok") version "8.6"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("checkstyle")
    id("org.sonarqube") version "6.0.1.5171"
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

val javalinVersion = "6.7.0"
val pangolinVersion = "42.7.2"
val jupyterVersion = "5.10.0"
val restAssuredVersion = "5.5.5"
val assertJVersion = "3.25.1"
val mockitoVersion = "5.18.0"
val mockWebServerVersion = "5.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.javalin:javalin:${javalinVersion}")
    implementation("gg.jte:jte:3.2.1")
    implementation("io.javalin:javalin-rendering:${javalinVersion}")
    implementation("org.slf4j:slf4j-simple:2.0.16")
    implementation("org.postgresql:postgresql:${pangolinVersion}")
    implementation("com.zaxxer:HikariCP:6.2.1")
    implementation("com.h2database:h2:2.3.232")
    implementation("org.jsoup:jsoup:1.21.1")
    implementation("com.konghq:unirest-java-core:4.5.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter:${jupyterVersion}")
    testImplementation("io.rest-assured:rest-assured:${restAssuredVersion}")
    testImplementation("org.assertj:assertj-core:${assertJVersion}")
    testImplementation("org.mockito:mockito-core:${mockitoVersion}")
    testImplementation("com.squareup.okhttp3:mockwebserver3:${mockWebServerVersion}")

}

application {
    mainClass.set("hexlet.code.App")
}

jacoco {
    toolVersion = "0.8.13"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
    finalizedBy(tasks.jacocoTestCoverageVerification)
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.10".toBigDecimal()
            }
        }
    }
}

sonar {
    properties {
        property("sonar.projectKey", "cheernomore_java-project-72")
        property("sonar.organization", "cheernomore")  // замените на вашу
        property("sonar.host.url", "https://sonarcloud.io")

        // Пути к исходникам
        property("sonar.sources", "src/main/java")
        property("sonar.tests", "src/test/java")

        // КЛЮЧЕВОЕ: путь к JaCoCo отчету
        property("sonar.coverage.jacoco.xmlReportPaths",
            "build/reports/jacoco/test/jacocoTestReport.xml")

        // Дополнительные настройки
        property("sonar.java.coveragePlugin", "jacoco")
        property("sonar.java.binaries", "build/classes")
        property("sonar.java.test.binaries", "build/classes")
    }
}


// Связываем задачи
tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.sonar {
    dependsOn(tasks.jacocoTestReport)
}
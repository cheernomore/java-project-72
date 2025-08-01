plugins {
    application
    id("io.freefair.lombok") version "8.6"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("checkstyle")
    id("org.sonarqube") version "6.0.1.5171"
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

val javalinVersion = "6.7.0"
val pangolinVersion = "42.7.2"

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

    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    mainClass.set("hexlet.code.App")
}

sonar {
    properties {
        property("sonar.projectKey", "cheernomore_java-project-72")
        property("sonar.organization", "cheernomore")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

tasks.test {
    useJUnitPlatform()
}
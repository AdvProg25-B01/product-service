import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    java
    jacoco
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.sonarqube") version "4.3.1.3277"
}

group = "id.ac.ui.cs.advprog"
version = "0.0.1-SNAPSHOT"

val versions = mapOf(
    "java" to "21",
    "jjwt" to "0.11.5",
    "springdoc" to "2.0.2",
    "jakartaAnnotation" to "2.1.1",
    "jakartaValidation" to "3.0.2",
    "postgresql" to "42.7.2",
    "h2" to "2.2.220",
    "junitJupiter" to "5.9.1",
    "seleniumJava" to "4.14.1",
    "seleniumJupiter" to "5.0.1",
    "webdrivermanager" to "5.6.3"
)

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(versions["java"]!!.toInt()))
    }
}

sonar {
    properties {
        property("sonar.projectKey", "AdvProg25-B01_product-service")
        property("sonar.organization", "advprog25-b01")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

tasks.withType<BootJar>().configureEach {
    destinationDirectory.set(layout.buildDirectory.dir("libs"))
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Jakarta APIs
    implementation("jakarta.annotation:jakarta.annotation-api:${versions["jakartaAnnotation"]}")
    implementation("jakarta.validation:jakarta.validation-api:${versions["jakartaValidation"]}")

    // API Docs
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${versions["springdoc"]}")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // JWT
    implementation("org.springframework.security:spring-security-crypto")
    implementation("io.jsonwebtoken:jjwt-api:${versions["jjwt"]}")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:${versions["jjwt"]}")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:${versions["jjwt"]}")

    // Database
    runtimeOnly("org.postgresql:postgresql:${versions["postgresql"]}")
    testImplementation("com.h2database:h2:${versions["h2"]}")
    runtimeOnly("com.h2database:h2")

    // Monitoring
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.junit.jupiter:junit-jupiter:${versions["junitJupiter"]}")
    testImplementation("org.seleniumhq.selenium:selenium-java:${versions["seleniumJava"]}")
    testImplementation("io.github.bonigarcia:selenium-jupiter:${versions["seleniumJupiter"]}")
    testImplementation("io.github.bonigarcia:webdrivermanager:${versions["webdrivermanager"]}")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.register<Test>("unitTest") {
    description = "Runs unit tests."
    group = "verification"
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
    useJUnitPlatform()
    filter { excludeTestsMatching("*FunctionalTest") }
}

tasks.register<Test>("functionalTest") {
    description = "Runs functional tests."
    group = "verification"
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
    useJUnitPlatform()
    filter { includeTestsMatching("*FunctionalTest") }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    systemProperty("spring.profiles.active", "test")
}

tasks.test {
    filter {
        excludeTestsMatching("*FunctionalTest")
    }
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
}
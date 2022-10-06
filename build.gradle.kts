import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    dependencies {
        classpath("org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:3.0")
    }
}

plugins {
    id("org.springframework.boot") version "2.6.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.sonarqube") version "3.0"
    id("org.jetbrains.dokka") version "1.6.0"
    jacoco

    kotlin("jvm") version "1.6.0"
    kotlin("plugin.spring") version "1.6.0"
}

group = "com.bory"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("io.projectreactor.addons:reactor-extra:3.4.5")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    implementation("io.jsonwebtoken:jjwt-api:0.11.2")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.2")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.2")

    implementation("com.github.ben-manes.caffeine:caffeine")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    runtimeOnly("dev.miku:r2dbc-mysql")
    runtimeOnly("mysql:mysql-connector-java")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("io.kotest:kotest-runner-junit5:5.0.1") {
        exclude("io.mockk", "mockk")
    }
    testImplementation("io.mockk:mockk:1.12.1")
    testImplementation("io.kotest:kotest-assertions-core:5.0.1")
    testImplementation("io.kotest:kotest-extensions-spring:4.4.3")

    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.6.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

sonarqube {
    properties {
        property("sonar.projectName", "kotlin-webflux-r2dbc")
        property("sonar.projecKey", "KWFR2")
        property("sonar.host.url", "http://localhost:9000")
        property("sonar.language", "kotlin")
        property("sonar.sources", "src/main/kotlin")
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.login", "admin")
        property("sonar.password", "admin")

        property("sonar.java.coveragePlugin", "jacoco")
        property(
            "sonar.jacoco.coverage.xmlReportPaths",
            "$buildDir/reports/jacoco/jacocoTestReport/jacocoTestReport.xml"
        )
        property("sonar.junit.reportsPaths", "$buildDir/test-results/")
    }
}

jacoco {
    toolVersion = "0.8.5"
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
        html.isEnabled = false
        csv.isEnabled = false
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            enabled = true
            element = "CLASS"
        }
    }
}

tasks.test {
    extensions.configure(JacocoTaskExtension::class) {
        setDestinationFile(file("$buildDir/jacoco/jacoco.exec"))
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks {
    named("jacocoTestReport", JacocoReport::class).get().dependsOn("test")
    named("jacocoTestCoverageVerification", JacocoCoverageVerification::class).get()
        .dependsOn("jacocoTestReport")
    named("sonarqube", org.sonarqube.gradle.SonarQubeTask::class).get()
        .dependsOn("jacocoTestCoverageVerification")
}

plugins {
    java
    application
    checkstyle
    jacoco
}

repositories {
    mavenCentral()
}

checkstyle {
    toolVersion = "10.16.0"
    setIgnoreFailures(false)
}

dependencies {
    implementation(libs.guava)

    // AWS SDK dependencies
    implementation("com.amazonaws:aws-lambda-java-core:1.2.3")
    implementation("com.amazonaws:aws-lambda-java-events:3.14.0")

    // JSON parsing dependencies
    // Chose Jackson over GSON for better performance and long term support.
    val jacksonVersion = "2.17.1"
    implementation("jakarta.annotation:jakarta.annotation-api:3.0.0")
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Use JUnit Jupiter for testing.
    testImplementation(libs.junit.jupiter)

    testImplementation("org.mockito:mockito-core:5.12.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.12.0")
    testImplementation("org.mockito:mockito-inline:5.2.0")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    // Define the main class for the application.
    mainClass = "com.consentframework.consenthistory.consentingestor.ConsentStreamIngestor"
}

tasks {
    withType<Test> {
        useJUnitPlatform()

        // Always run jacoco test report after tests
        finalizedBy(jacocoTestReport)
    }

    jacocoTestCoverageVerification {
        violationRules {
            rule {
                limit {
                    minimum = BigDecimal.valueOf(0.95)
                }
            }
        }
    }

    check {
        // Fail build if under min test coverage thresholds
        dependsOn(jacocoTestCoverageVerification)
    }
}

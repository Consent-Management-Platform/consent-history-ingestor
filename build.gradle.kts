plugins {
    java
    application
    jacoco

    id("com.consentframework.consentmanagement.checkstyle-config") version "1.1.0"
}

repositories {
    mavenCentral()
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/Consent-Management-Platform/consent-api-java-common")
            credentials {
                username = project.findProperty("gpr.usr") as String? ?: System.getenv("GITHUB_USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
    gradlePluginPortal()
}

dependencies {
    implementation(libs.guava)

    // Common Consent Framework API Java libraries
    implementation("com.consentframework:api-java-common:0.0.13")

    // AWS Lambda SDK
    implementation("com.amazonaws:aws-lambda-java-core:1.2.3")
    implementation("com.amazonaws:aws-lambda-java-events:3.14.0")

    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.1")

    // AWS DynamoDB SDK
    val dynamoDbClientVersion = "2.26.7"
    implementation("software.amazon.awssdk:dynamodb:$dynamoDbClientVersion")
    implementation("software.amazon.awssdk:dynamodb-enhanced:$dynamoDbClientVersion")

    // Logging
    val log4j2Version = "2.24.3"
    implementation("org.apache.logging.log4j:log4j-api:$log4j2Version")
    implementation("org.apache.logging.log4j:log4j-core:$log4j2Version")

    // Use JUnit Jupiter for testing.
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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
                    minimum = BigDecimal.valueOf(0.94)
                }
            }
        }
    }

    check {
        // Fail build if under min test coverage thresholds
        dependsOn(jacocoTestCoverageVerification)
    }
}

// Build jar which will later be consumed to run the application
tasks.register<Zip>("packageJar") {
    into("lib") {
        from(tasks.jar)
        from(configurations.runtimeClasspath)
    }
}

tasks.build {
    dependsOn("packageJar")
}

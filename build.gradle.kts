plugins {
    java
    application
    jacoco

    id("com.consentframework.consentmanagement.checkstyle-config") version "1.1.0"
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.guava)

    // AWS Lambda SDK
    implementation("com.amazonaws:aws-lambda-java-core:1.2.3")
    implementation("com.amazonaws:aws-lambda-java-events:3.14.0")

    // AWS DynamoDB SDK
    val dynamoDbClientVersion = "2.26.7"
    implementation("software.amazon.awssdk:dynamodb:$dynamoDbClientVersion")
    implementation("software.amazon.awssdk:dynamodb-enhanced:$dynamoDbClientVersion")

    // JSON parsing dependencies
    // Chose Jackson over GSON for better performance and long term support.
    val jacksonVersion = "2.17.1"
    implementation("jakarta.annotation:jakarta.annotation-api:3.0.0")
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")

    // Logging
    val log4j2Version = "2.24.3"
    implementation("org.apache.logging.log4j:log4j-api:$log4j2Version")
    implementation("org.apache.logging.log4j:log4j-core:$log4j2Version")

    // Immutables
    val immutablesDependency = "org.immutables:value:2.10.1"
    compileOnly(immutablesDependency)
    annotationProcessor(immutablesDependency)
    testCompileOnly(immutablesDependency)
    testAnnotationProcessor(immutablesDependency)

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

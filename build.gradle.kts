plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.10"
    id("antlr")
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.7"
    application
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    antlr("org.antlr:antlr4:4.5")

    // Use the Kotlin JUnit 5 integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

    // Use the JUnit 5 integration.
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.3")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // This dependency is used by the application.
    implementation("com.google.guava:guava:32.1.1-jre")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(20))
    }
}

application {
    // Define the main class for the application.
    mainClass.set("edu.itmo.ilang.AppKt")
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}

tasks.generateGrammarSource {
    arguments = arguments + listOf("-visitor", "-long-messages")
}

tasks.named("compileKotlin") {
    dependsOn(tasks.named("generateGrammarSource"))
}

tasks.named("compileTestKotlin") {
    dependsOn(tasks.named("generateTestGrammarSource"))
}

task<JavaExec>("generateTests") {
    group = "verification"
    classpath = sourceSets["test"].runtimeClasspath
    mainClass = "TestsGeneratorKt"
}

tasks.test {
    testLogging {
        events("passed", "skipped", "failed", "standardOut", "standardError")
    }
}

import org.jetbrains.kotlin.util.capitalizeDecapitalize.toLowerCaseAsciiOnly

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.10"
    id("antlr")
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.7"
    id("org.bytedeco.gradle-javacpp-platform") version ("1.5.10")
    application
}

val llvmVersion = "17.0.6-1.5.10"

ext {
    this.set("javacppPlatform", getJavacppPlatformClassifier())
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
    implementation("org.bytedeco:llvm-platform:17.0.6-1.5.10")
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

/**
 * Some of available classifiers are here
 * https://github.com/bytedeco/javacpp-presets/blob/master/llvm/platform/pom.xml
 */
fun getJavacppPlatformClassifier(): String {
    val currentOSString = System.getProperty("os.name").toLowerCaseAsciiOnly()
    val osArch = System.getProperty("os.arch")

    return when {
        currentOSString.contains("win") -> "windows-x86_64"
        currentOSString.contains("linux") -> {
            when {
                osArch.contains("aarch64") -> "linux-arm64"
                else -> "linux-x86_64"
            }
        }
        currentOSString.contains("mac") -> {
            when {
                osArch.contains("aarch64") -> "macosx-arm64"
                else -> "macosx-x86_64"
            }
        }
        else -> error("unsupported os $currentOSString and architecture $osArch")
    }
}

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

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

repositories {
    mavenCentral()
}

dependencies {
    antlr("org.antlr:antlr4:4.5")

    implementation("com.google.guava:guava:32.1.1-jre")
    implementation("org.bytedeco:llvm-platform:17.0.6-1.5.10")
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.5")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation(kotlin("test"))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(20))
    }
}

application {
    mainClass.set("edu.itmo.ilang.AppKt")
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
    outputs.upToDateWhen { false }

    useJUnitPlatform()
    jvmArgs("--enable-native-access=ALL-UNNAMED")

    if (project.hasProperty("excludeTests")) {
        exclude(project.property("excludeTests").toString().split(","))
    }

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

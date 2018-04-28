
import com.github.spotbugs.SpotBugsTask
import org.gradle.api.tasks.wrapper.Wrapper

plugins {
    java
    id("com.diffplug.gradle.spotless") version "3.10.0"
    id("com.github.spotbugs") version "1.6.1"
}

apply {
    plugin("com.github.spotbugs")
}

// Spotless is used to lint and reformat source files.
spotless {
    kotlinGradle {
        // Configure the formatting of the Gradle Kotlin DSL files (*.gradle.kts)
        ktlint()
        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }
    // Need to disable freshmark for now because latest release is broken
    //freshmark {
    //    trimTrailingWhitespace()
    //    indentWithSpaces()
    //    endWithNewline()
    //}
    format("extraneous") {
        target("*.xml", "*.yml")
        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(group = "com.diffplug.durian", name = "durian-core", version = "1.2.0")
    implementation(group = "com.diffplug.jscriptbox", name = "jscriptbox", version = "3.0.0")
    implementation(group = "args4j", name = "args4j", version = "2.33")
    implementation(group = "com.github.spotbugs", name = "spotbugs-annotations", version = "3.1.3")

    fun junitJupiter(name: String, version: String = "5.1.1") =
            create(group = "org.junit.jupiter", name = name, version = version)

    testImplementation(junitJupiter(name = "junit-jupiter-api"))
    testImplementation(junitJupiter(name = "junit-jupiter-engine"))
    testImplementation(junitJupiter(name = "junit-jupiter-params"))
    testImplementation(junitJupiter(name = "junit-jupiter-migrationsupport"))
}

spotbugs {
    sourceSets = setOf(java.sourceSets["main"], java.sourceSets["test"])
    toolVersion = "3.1.3"
    effort = "max"
}

tasks.withType<SpotBugsTask> {
    reports {
        xml.isEnabled = false
        emacs.isEnabled = true
    }
    finalizedBy(task("${name}Report") {
        mustRunAfter(this@withType)
        doLast {
            this@withType
                    .reports
                    .emacs
                    .destination
                    .takeIf { it.exists() }
                    ?.readText()
                    .takeIf { !it.isNullOrBlank() }
                    ?.also { logger.warn(it) }
        }
    })
}

tasks.withType<Test> {
    useJUnitPlatform()
}

task<Wrapper>("wrapper") {
    gradleVersion = "4.7"
}

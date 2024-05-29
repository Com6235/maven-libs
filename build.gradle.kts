import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import java.net.URI

plugins {
    kotlin("jvm") version "1.9.24"
    id("org.jetbrains.dokka") version "1.9.20"
    idea
    `maven-publish`
    signing
    id("org.jlleitschuh.gradle.ktlint") version "11.5.0"
}

group = "io.github.com6235"
version = "root"

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "idea")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    repositories {
        mavenCentral()
    }

    dependencies {
        testImplementation(kotlin("test"))
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }

    kotlin {
        jvmToolchain(17)
    }

    idea {
        module {
            isDownloadJavadoc = true
            isDownloadSources = true
        }
    }

    ktlint {
        version.set("0.50.0")
        debug.set(true)
        verbose.set(true)
        android.set(false)
        outputToConsole.set(true)
        outputColorName.set("RED")
        ignoreFailures.set(false)
        enableExperimentalRules.set(true)
        reporters {
            reporter(ReporterType.HTML)
        }
    }

    tasks.register<Jar>("dokkaJavadocJar") {
        group = "jar"
        dependsOn(tasks.dokkaJavadoc)
        from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
        archiveClassifier.set("javadoc")
    }

    tasks.register<Jar>("sourcesJar") {
        group = "jar"
        from(sourceSets.main.get().allSource)
        archiveClassifier.set("sources")
    }

    configurations {
        create("javadoc")
        create("sources")
    }

    publishing {
        repositories {
            maven {
                name = "GitHubPackages"
                url = URI("https://maven.pkg.github.com/com6235/maven-libs")
                credentials {
                    username = System.getenv("GITHUB_ACTOR")
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }

    tasks.withType<PublishToMavenRepository> {
        onlyIf {
            !project.version.toString().endsWith("-SNAPSHOT")
        }
    }
}

tasks.jar {
    enabled = false
}

ktlint {
    version.set("0.50.0")
    debug.set(true)
    verbose.set(true)
    android.set(false)
    outputToConsole.set(true)
    outputColorName.set("RED")
    ignoreFailures.set(false)
    enableExperimentalRules.set(true)
    reporters {
        reporter(ReporterType.HTML)
    }
}

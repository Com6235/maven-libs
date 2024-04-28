import java.net.URI

plugins {
    kotlin("jvm") version "1.9.23"
    id("io.freefair.lombok") version "8.6"
    id("org.jetbrains.dokka") version "1.9.20"
    idea
    `maven-publish`
}

group = "io.github.com6235"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(kotlin("reflect"))

    implementation("org.telegram:telegrambots-longpolling:7.2.1")
    implementation("org.telegram:telegrambots-webhook:7.2.1")
    implementation("org.telegram:telegrambots-client:7.2.1")
    implementation("org.telegram:Bots:7.2.1")

    implementation("org.slf4j:slf4j-api:2.0.13")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

tasks.register<Jar>("dokkaJavadocJar") {
    dependsOn(tasks.dokkaJavadoc)
    from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

configurations {
    create("javadoc")
}

val jdFile = layout.buildDirectory.file("libs/$name-$version-javadoc.jar")
val jdArtifact = artifacts.add("javadoc", jdFile.get().asFile) {
    type = "jar"
    builtBy("dokkaJavadocJar")
}

publishing {
    publications {
        create<MavenPublication>("kotlin") {
            from(components["kotlin"])
            artifact(jdArtifact)
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = URI("https://maven.pkg.github.com/com6235/tgbotter")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
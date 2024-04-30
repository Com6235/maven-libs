import java.net.URI

plugins {
    kotlin("jvm") version "1.9.23"
    id("io.freefair.lombok") version "8.6"
    id("org.jetbrains.dokka") version "1.9.20"
    idea
    `maven-publish`
    signing
}

group = "io.github.com6235"
version = "1.0.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    api("org.telegram:telegrambots-longpolling:7.2.1")
    api("org.telegram:telegrambots-webhook:7.2.1")
    api("org.telegram:telegrambots-client:7.2.1")
    api("org.telegram:telegrambots-meta:7.2.1")

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

tasks.register<Jar>("sourcesJar") {
    from(sourceSets.main.get().allSource)
    archiveClassifier.set("sources")
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

configurations {
    create("javadoc")
    create("sources")
}

val jdFile = layout.buildDirectory.file("libs/$name-$version-javadoc.jar")
val jdArtifact = artifacts.add("javadoc", jdFile.get().asFile) {
    type = "jar"
    builtBy("dokkaJavadocJar")
}

val srcFile = layout.buildDirectory.file("libs/$name-$version-sources.jar")
val srcArtifact = artifacts.add("sources", srcFile.get().asFile) {
    type = "jar"
    builtBy("sourcesJar")
}

publishing {
    publications {
        create<MavenPublication>("kotlin") {
            from(components["kotlin"])
            artifact(jdArtifact)
            artifact(srcArtifact)
            pom {
                packaging = "jar"
                groupId = group.toString()
                artifactId = project.name
                version = project.version.toString()
                name = "${group}:${project.name}"
                description = "A framework for creating Telegram bots with ease. Made using official Telegram API"
                url = "https://github.com/Com6235/tgBotter"
                licenses {
                    license {
                        name = "MIT License"
                        url = "https://opensource.org/license/mit"
                    }
                }
                developers {
                    developer {
                        id = "com6235"
                        name = "Com6235"
                    }
                }
                scm {
                    connection = "scm:git:git://github.com/Com6235/tgBotter.git"
                    developerConnection = "scm:git:ssh://github.com:Com6235/tgBotter.git"
                    url = "https://github.com/Com6235/tgBotter"
                }
            }
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

signing {
    if (System.getenv("IS_CI") == null) {
        useGpgCmd()
        sign(publishing.publications["kotlin"])
    }
}

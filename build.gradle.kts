plugins {
    kotlin("jvm") version "1.9.23"
    id("io.freefair.lombok") version "8.6"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("org.jetbrains.dokka") version "1.9.20"
    idea
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

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}
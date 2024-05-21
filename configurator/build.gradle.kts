group = "io.github.com6235"
version = "1.0"

plugins {
    kotlin("plugin.serialization") version "2.0.0-RC3"
}

val serializationVersion = "1.6.3"

dependencies {
    testImplementation(kotlin("test"))

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-hocon:$serializationVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-properties:$serializationVersion")
    implementation("com.charleskorn.kaml:kaml:0.59.0")
    implementation("net.peanuuutz.tomlkt:tomlkt:0.3.7")
}
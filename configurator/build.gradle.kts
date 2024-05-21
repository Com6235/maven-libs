group = "io.github.com6235"
version = "1.0.1"

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
        create<MavenPublication>("configurator") {
            from(components["kotlin"])
            artifact(jdArtifact)
            artifact(srcArtifact)
            pom {
                packaging = "jar"
                groupId = group.toString()
                artifactId = project.name
                version = project.version.toString()
                name = "${group}:${project.name}"
                description = "Thing to automatically load configs, supports creating custom loaders"
                url = "https://github.com/Com6235/maven-libs"
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
                    connection = "scm:git:git://github.com/Com6235/maven-libs.git"
                    developerConnection = "scm:git:ssh://github.com:Com6235/maven-libs.git"
                    url = "https://github.com/Com6235/maven-libs"
                }
            }
        }
    }
}

signing {
    if (System.getenv("IS_CI") == null) {
        useGpgCmd()
        sign(publishing.publications["configurator"])
    }
}
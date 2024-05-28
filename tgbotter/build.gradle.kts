group = "io.github.com6235"
version = "1.0.4-SNAPSHOT"

val telegramVersion = "7.2.1"

dependencies {
    implementation("org.telegram:telegrambots-longpolling:$telegramVersion")
    implementation("org.telegram:telegrambots-webhook:$telegramVersion")
    implementation("org.telegram:telegrambots-client:$telegramVersion")
    api("org.telegram:telegrambots-meta:$telegramVersion")

    implementation("org.slf4j:slf4j-api:2.0.13")
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
        create<MavenPublication>("tgbotter") {
            from(components["kotlin"])
            artifact(jdArtifact)
            artifact(srcArtifact)
            pom {
                packaging = "jar"
                groupId = group.toString()
                artifactId = project.name
                version = project.version.toString()
                name = "$group:${project.name}"
                description = "A framework for creating Telegram bots with ease. Made using official Telegram API"
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
        sign(publishing.publications["tgbotter"])
    }
}

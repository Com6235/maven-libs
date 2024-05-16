# Maven-Libs

A collection of my libraries for Java/Kotlin.
All packages will be published to [GitHub Packages](https://docs.github.com/ru/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#authenticating-with-a-personal-access-token),
so you can see all my libs in one place.

## Using my libs

Since the packages are hosted on GitHub Packages, to download them,
you will need to add my GitHub Packages Maven repository to your repositories:

### In Maven
To add repositories in Maven, you need to edit your `~\.m2\settings.xml`

It should look something like this:
```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">

  <activeProfiles>
    <activeProfile>github</activeProfile>
  </activeProfiles>

  <profiles>
    <profile>
      <id>github</id>
      <repositories>
          <!-- other repos -->
        <repository>
          <id>github tgbotter repository</id>
          <url>https://maven.pkg.github.com/com6235/maven-libs</url>
        </repository>
          <!-- other repos -->
      </repositories>
    </profile>
  </profiles>

  <servers>
    <server>
      <id>github</id>
      <username>YOUR_GITHUB_USERNAME</username>
      <password>YOUR_GITHUB_TOKEN</password>
    </server>
  </servers>
</settings>
```

### In Gradle

In `build.gradle.kts`:
```kotlin
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/com6235/maven-libs")
        credentials {
            // this is just example code, you can change it to just strings if you are not going to publish your code
            username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME") // your GitHub username
            password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN") // your GitHub token
        }
    }
}
```

In `build.gradle`:
```groovy
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/com6235/maven-libs")
        credentials {
            // this is just example code, you can change it to just strings if you are not going to publish your code
            username = project.findProperty("gpr.user") ?: System.getenv("USERNAME") // your GitHub username
            password = project.findProperty("gpr.key") ?: System.getenv("TOKEN") // your GitHub token
        }
   }
}
```
Your token MUST be a [classic token](https://github.com/settings/tokens), and it MUST have `read:packages` scope

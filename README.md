# Tgbotter

A Kotlin framework for creating telegram bots with ease. Made using [official Telegram Bots Java API](https://github.com/rubenlagus/TelegramBots).

Latest version: https://github.com/Com6235/tgBotter/packages/2135012

## Installation
### Maven

Since the packages are hosted on GitHub Packages, to download them, you will need to add my GitHub Packages Maven repository to your `~\.m2\settings.xml`
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
          <url>https://maven.pkg.github.com/com6235/tgbotter</url>
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

Your token MUST be a [classic token](https://github.com/settings/tokens), and it MUST have a `read:packages` permission

After you added the repository, you can start using my package by adding this to your `pom.xml`
```xml
<dependency>
    <groupId>io.github.com6235</groupId>
    <artifactId>tgbotter</artifactId>
    <version>${your desired version}</version>
</dependency>
```

More information on GitHub Packages Maven Registry is [here](https://docs.github.com/ru/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#authenticating-with-a-personal-access-token)

### Gradle

Since the packages are hosted on GitHub Packages, to download them, you will need to add my GitHub Packages Maven repository to your `repositories`:

In `build.gradle.kts`:
```kotlin
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/com6235/tgbotter")
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
        url = uri("https://maven.pkg.github.com/com6235/tgbotter")
        credentials {
            // this is just example code, you can change it to just strings if you are not going to publish your code
            username = project.findProperty("gpr.user") ?: System.getenv("USERNAME") // your GitHub username
            password = project.findProperty("gpr.key") ?: System.getenv("TOKEN") // your GitHub token
        }
   }
}
```
Your token MUST be a [classic token](https://github.com/settings/tokens), and it MUST have a `read:packages` permission

After you added the repository, you can start using my package by adding this to `dependencies`:

In `build.gradle.kts`:
```kotlin
dependencies {
    implementation("io.github.com6235:tgbotter:${your desired version}")
}
```

In `build.gradle`:
```groovy
dependencies {
    implementation 'io.github.com6235:tgbotter:${your desired version}'
}
```

## Examples

### Echo bot

```kotlin
import io.github.com6235.tgbotter.*
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.generics.TelegramClient

fun main() {
    // Create a LongPollingBot
    val bot = LongPollingBot(BotCreationOptions("your token"))

    // Add a listener
    bot.addListener(Handler())

    // Start the bot
    bot.start()
}

// Define a listener
class Handler : Listener {
    // Make an event handler
    override fun onMessage(message: Message, telegramClient: TelegramClient) {
        // Send a message using telegramClient
        telegramClient.execute(
            SendMessage.builder().text(message.text).chatId(message.chatId).replyToMessageId(message.messageId).build()
        )
    }
}
```

### Using the command handler

```kotlin
import io.github.com6235.tgbotter.*
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

fun main() {
    // Create a LongPollingBot
    val bot = LongPollingBot(BotCreationOptions("your token"))

    // Add commands
    bot.commandManager.addCommand(
        Command("start") { handleStart(this) }
    )
    bot.commandManager.addCommand(
        Command("help") { handleInfo(this) }
    )

    // Start the bot
    bot.start()
}

fun handleStart(commandHandler: CommandHandler) {
    commandHandler.telegramClient.execute(
        SendMessage.builder()
            .chatId(commandHandler.message.chatId)
            .text("Hello! Send /help to view the help page.")
            .build()
    )
}

fun handleInfo(commandHandler: CommandHandler) {
    commandHandler.telegramClient.execute(
        SendMessage.builder()
            .chatId(commandHandler.message.chatId)
            .text("A very useful help page!")
            .build()
    )
}
```

## Building from sources

### Requirements

- JDK 17
- [GnuPG](https://www.gnupg.org/) (for signing publications)

### Building the project

Use commands
```bash
.\gradlew build
.\gradlew sourcesJar
.\gradlew dokkaJavadocJar
```
to build all the needed jar-files, or you can do:
```bash
.\gradlew signKotlinPublication
```
to automatically build and sign everything

### Publishing to local Maven repository

Use command
```bash
.\gradlew publishToMavenLocal
```
to publish all the [artifacts](#building-the-project) to your local Maven repository _(~/.m2/repository)_
# Tgbotter

A Kotlin framework for creating telegram bots with ease. Made using [official Telegram Bots Java API](https://github.com/rubenlagus/TelegramBots).

Latest version: https://github.com/Com6235/maven-libs/packages/2135012

## Using as a dependency
### Maven

After you added the repository, you can start using my package by adding this to your `pom.xml`
```xml
<dependency>
    <groupId>io.github.com6235</groupId>
    <artifactId>tgbotter</artifactId>
    <version>${your desired version}</version>
</dependency>
```

### Gradle

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
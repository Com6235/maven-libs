package io.github.com6235.tgbotter

import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.generics.TelegramClient

/**
 * Options for creating bots.
 *
 * @property token Bot token
 * @property runCommandsThroughOnMessage Should commands be run through onMessage event?
 */
data class BotCreationOptions(
    val token: String,
    val botName: String,
    val botDescription: String = "",
    val botShortDescription: String = "",
    val runCommandsThroughOnMessage: Boolean = false,
    val logUpdates: Boolean = false,
    val loggerName: String = LongPollingBot::class.qualifiedName!!,
)

/**
 * Class for commands
 *
 * @property name Name of the command, so bot will be able to identify it
 * @property handler Command handler
 */
data class Command(val name: String, val description: String, val handler: CommandHandler.() -> Unit)

/**
 * Class, that is given to all commands.
 *
 * @property message Message with the command
 * @property telegramClient Client of the bot
 */
data class CommandHandler(val message: Message, val telegramClient: TelegramClient)
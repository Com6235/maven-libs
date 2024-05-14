package io.github.com6235.tgbotter.common

import io.github.com6235.tgbotter.LongPollingBot
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.generics.TelegramClient

/**
 * Options for creating bots.
 *
 * @property token Bot token
 * @property botName Bot's name (doesn't update name if `null`)
 * @property botDescription Bot's description (doesn't update name if `null`)
 * @property botShortDescription Bot's short description (doesn't update name if `null`)
 * @property runCommandsThroughOnMessage Should commands be run through onMessage event?
 * @property logUpdates Should bot log all updates?
 * @property loggerName Name for bot's SLF4J logger
 */
data class BotCreationOptions(
    val token: String,
    val botName: String? = null,
    val botDescription: String? = null,
    val botShortDescription: String? = null,
    val runCommandsThroughOnMessage: Boolean = false,
    val logUpdates: Boolean = false,
    val loggerName: String = LongPollingBot::class.qualifiedName!!,
)

/**
 * Class for commands
 *
 * @property name Name of the command, so bot will be able to identify it
 * @property description Description of the command that will be shown in Telegram's command menu
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
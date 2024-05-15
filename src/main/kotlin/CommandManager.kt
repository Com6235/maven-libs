package io.github.com6235.tgbotter

import io.github.com6235.tgbotter.common.Bot
import io.github.com6235.tgbotter.common.Command
import io.github.com6235.tgbotter.common.CommandHandler
import io.github.com6235.tgbotter.common.Listener
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.generics.TelegramClient

/**
 * Manager for all commands. Runs commands first, before any other event
 */
class CommandManager(private val bot: Bot) {
    internal val handle = Handle(this.bot.telegramClient)
    internal val commandRegex = Regex("[a-z0-9_]+")

    /**
     * Adds a [Command] to bot's command list.
     */
    fun addCommand(vararg commands: Command) {
        Handle.commands.addAll(commands)
        for (command in commands) {
            if (commandRegex.matchEntire(command.name) == null) {
                bot.logger.error("${command.name} has an invalid name! " +
                        "Command's name should contain only lowercase english letters, digits and underscores. " +
                        "${command.name} will not be registered as a valid command in Telegram command list."
                )
            }
        }
    }

    internal class Handle(private val telegramClient: TelegramClient) : Listener {
        override fun onMessage(message: Message, telegramClient: TelegramClient) {
            val command = message.text.split(" ")
            val handler = commands.firstOrNull { "/" + it.name == command[0] } ?: return
            handler.handler(CommandHandler(message, telegramClient))
        }

        companion object {
            internal val commands: MutableList<Command> = mutableListOf()
        }
    }
}

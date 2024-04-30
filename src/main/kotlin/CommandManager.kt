package io.github.com6235.tgbotter

import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.generics.TelegramClient

/**
 * Manager for all commands. Runs commands first, before any other event
 */
class CommandManager(private val telegramClient: TelegramClient) {
    internal val handle = Handle(this.telegramClient)

    /**
     * Adds a [Command] to bot's command list. Does not update the menu in Telegram.
     */
    fun addCommand(command: Command) = Handle.commands.add(command)

    /**
     * Removes a [Command] from bot's command list. Requires [command] to be THE SAME variable (not command) that was added
     *
     * @see addCommand
     */
    fun removeCommand(command: Command) = Handle.commands.remove(command)

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

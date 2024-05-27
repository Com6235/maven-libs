package io.github.com6235.tgbotter.common

import io.github.com6235.tgbotter.CommandManager
import io.github.com6235.tgbotter.CommandManager.Handle.Companion.commands
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands
import org.telegram.telegrambots.meta.api.methods.description.GetMyDescription
import org.telegram.telegrambots.meta.api.methods.description.GetMyShortDescription
import org.telegram.telegrambots.meta.api.methods.description.SetMyDescription
import org.telegram.telegrambots.meta.api.methods.description.SetMyShortDescription
import org.telegram.telegrambots.meta.api.methods.name.GetMyName
import org.telegram.telegrambots.meta.api.methods.name.SetMyName
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand
import org.telegram.telegrambots.meta.generics.TelegramClient

abstract class Bot(private val options: BotCreationOptions) {
    internal val telegramClient: TelegramClient = OkHttpTelegramClient(this.options.token)
    protected abstract val application: AutoCloseable
    private val listeners: MutableList<Listener> = mutableListOf()
    internal val logger = LoggerFactory.getLogger(options.loggerName)

    /**
     * This bot's command manager.
     * Commands run first, before any other listener.
     * Also commands doesn't run through onMessage event (change with [BotCreationOptions.runCommandsThroughOnMessage])
     */
    val commandManager = CommandManager(this)

    init {
        this.listeners.add(commandManager.handle)
    }

    protected fun setNameAndCommands(hookStopToShutdown: Boolean, autoSetCommands: Boolean) {
        val commandsToSend = mutableListOf<BotCommand>()
        for (command in commands) {
            if (commandManager.commandRegex.matchEntire(command.name) != null) {
                commandsToSend.add(BotCommand(command.name, command.description))
            }
        }
        if (commandsToSend.isNotEmpty() && autoSetCommands)
            telegramClient.execute(SetMyCommands.builder().commands(commandsToSend).build())

        val name: String = try {
            telegramClient.execute(GetMyName.builder().build()).name
        } catch (_: Exception) { options.botName ?: "" }

        val description: String = try {
            telegramClient.execute(GetMyDescription.builder().build()).description
        } catch (_: Exception) { options.botDescription ?: "" }

        val shortDescription: String = try {
            telegramClient.execute(GetMyShortDescription.builder().build()).shortDescription
        } catch (_: Exception) { options.botName ?: "" }

        if (options.botName != null && options.botName != name)
            telegramClient.execute(SetMyName.builder().name(options.botName).build())

        if (options.botDescription != null && options.botDescription != description)
            telegramClient.execute(
                SetMyDescription.builder()
                .description(options.botDescription)
                .build()
            )
        if (options.botShortDescription != null && options.botShortDescription != shortDescription)
            telegramClient.execute(
                SetMyShortDescription.builder()
                .shortDescription(options.botShortDescription)
                .build()
            )

        if (hookStopToShutdown) {
            Runtime.getRuntime().addShutdownHook(Thread {
                stop()
            })
        }
    }

    /**
     * Adds a listener to the bot, so the bot can work with it
     *
     * @see Listener
     */
    fun addListener(vararg listener: Listener) { this.listeners.addAll(listener) }
    internal fun getListeners(): MutableList<Listener> {
        val s = mutableListOf<Listener>(); s.addAll(this.listeners)
        return s
    }

    /**
     * Starts the bot session.
     */
    abstract fun start(autoSetCommands: Boolean = true, hookStopToShutdown: Boolean = false, exitOnError: Boolean = true)

    /**
     * Stops the bot session
     */
    abstract fun stop()
}

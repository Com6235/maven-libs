package io.github.com6235.tgbotter

import io.github.com6235.tgbotter.CommandManager.Handle.Companion.commands
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.longpolling.BotSession
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer
import org.telegram.telegrambots.meta.api.methods.commands.DeleteMyCommands
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands
import org.telegram.telegrambots.meta.api.methods.description.SetMyDescription
import org.telegram.telegrambots.meta.api.methods.description.SetMyShortDescription
import org.telegram.telegrambots.meta.api.methods.name.SetMyName
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Class for creating long-polling bots using Telegram Bot API
 */
class LongPollingBot(private val options: BotCreationOptions) {
    internal val telegramClient: TelegramClient = OkHttpTelegramClient(this.options.token)
    private val application = TelegramBotsLongPollingApplication()
    private val listeners: MutableList<Listener> = mutableListOf()
    internal val logger = LoggerFactory.getLogger(options.loggerName)
    private lateinit var botSession: BotSession

    /**
     * This bot's command manager.
     * Commands run first, before any other listener (change with [BotCreationOptions.runCommandsThroughOnMessage])
     */
    val commandManager = CommandManager(this)

    init {
        this.listeners.add(commandManager.handle)
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
    fun start() {
        try {
            botSession = application.registerBot(this.options.token, Consumer(this))

            val commandsToSend = mutableListOf<BotCommand>()
            for (command in commands) {
                if (commandManager.commandRegex.matchEntire(command.name) != null) {
                    commandsToSend.add(BotCommand(command.name, command.description))
                }
            }
            telegramClient.execute(SetMyCommands.builder().commands(commandsToSend).build())
            if (options.botName != null) telegramClient.execute(SetMyName.builder().name(options.botName).build())
            if (options.botDescription != null) telegramClient.execute(SetMyDescription.builder()
                .description(options.botDescription)
                .build()
            )
            if (options.botShortDescription != null) telegramClient.execute(SetMyShortDescription.builder()
                .shortDescription(options.botShortDescription)
                .build()
            )

            botSession.start()
            logger.info("Bot started successfully!")
        } catch (e: TelegramApiException) {
            logger.error("There was an error starting the bot: ${e.message}")
        }
    }

    /**
     * Stops the bot session
     */
    fun stop() {
        try {
            botSession.stop()
            telegramClient.execute(DeleteMyCommands.builder().build())
            application.unregisterBot(this.options.token)
            logger.info("Bot stopped successfully!")
        } catch (e: TelegramApiException) {
            logger.error("There was an error stopping the bot: ${e.message}")
        }

    }

    private class Consumer(private val bot: LongPollingBot) : LongPollingUpdateConsumer {
        private val updatesProcessorExecutor: Executor = Executors.newSingleThreadExecutor()

        override fun consume(updates: List<Update>) {
            updates.forEach {
                updatesProcessorExecutor.execute { consume(it) }
            }
        }

        fun consume(update: Update) {
            if (update.hasMessage() && update.message.hasText() && update.message.text.startsWith("/")) {
                bot.getListeners().first().onMessage(update.message, bot.telegramClient)
                afterUpdate(update, "Command", null)
                if (!bot.options.runCommandsThroughOnMessage) {
                    return
                }
            }
            bot.getListeners().subList(1, bot.getListeners().size).forEach {
                when {
                    update.hasBusinessConnection() -> {
                        it.onBusinessConnection(update.businessConnection, bot.telegramClient)
                        afterUpdate(update, "BusinessConnection", it)
                    }
                    update.hasBusinessMessage() -> {
                        it.onBusinessMessage(update.businessMessage, bot.telegramClient)
                        afterUpdate(update, "BusinessMessage", it)
                    }
                    update.hasCallbackQuery() -> {
                        it.onCallbackQuery(update.callbackQuery, bot.telegramClient)
                        afterUpdate(update, "CallbackQuery", it)
                    }
                    update.hasChannelPost() -> {
                        it.onChannelPost(update.channelPost, bot.telegramClient)
                        afterUpdate(update, "ChannelPost", it)
                    }
                    update.hasChatJoinRequest() -> {
                        it.onChatJoinRequest(update.chatJoinRequest, bot.telegramClient)
                        afterUpdate(update, "ChatJoinRequest", it)
                    }
                    update.hasChatMember() -> {
                        it.onChatMember(update.chatMember, bot.telegramClient)
                        afterUpdate(update, "ChatMember", it)
                    }
                    update.hasChosenInlineQuery() -> {
                        it.onChosenInlineQuery(update.chosenInlineQuery, bot.telegramClient)
                        afterUpdate(update, "ChosenInlineQuery", it)
                    }
                    update.hasDeletedBusinessMessage() -> {
                        it.onDeletedBusinessMessage(update.deletedBusinessMessages, bot.telegramClient)
                        afterUpdate(update, "DeletedBusinessMessage", it)
                    }
                    update.hasEditedBusinessMessage() -> {
                        it.onEditedBusinessMessage(update.editedBuinessMessage, bot.telegramClient)
                        afterUpdate(update, "EditedBusinessMessage", it)
                    }
                    update.hasEditedChannelPost() -> {
                        it.onEditedChannelPost(update.editedChannelPost, bot.telegramClient)
                        afterUpdate(update, "EditedChannelPost", it)
                    }
                    update.hasEditedMessage() -> {
                        it.onEditedMessage(update.editedMessage, bot.telegramClient)
                        afterUpdate(update, "EditedMessage", it)
                    }
                    update.hasInlineQuery() -> {
                        it.onInlineQuery(update.inlineQuery, bot.telegramClient)
                        afterUpdate(update, "InlineQuery", it)
                    }
                    update.hasMessage() -> {
                        it.onMessage(update.message, bot.telegramClient)
                        afterUpdate(update, "Message", it)
                    }
                    update.hasMyChatMember() -> {
                        it.onMyChatMember(update.myChatMember, bot.telegramClient)
                        afterUpdate(update, "MyChatMember", it)
                    }
                    update.hasPoll() -> {
                        it.onPoll(update.poll, bot.telegramClient)
                        afterUpdate(update, "Poll", it)
                    }
                    update.hasPollAnswer() -> {
                        it.onPollAnswer(update.pollAnswer, bot.telegramClient)
                        afterUpdate(update, "PollAnswer", it)
                    }
                    update.hasPreCheckoutQuery() -> {
                        it.onPreCheckoutQuery(update.preCheckoutQuery, bot.telegramClient)
                        afterUpdate(update, "PreCheckoutQuery", it)
                    }
                    update.hasShippingQuery() -> {
                        it.onShippingQuery(update.shippingQuery, bot.telegramClient)
                        afterUpdate(update, "ShippingQuery", it)
                    }
                }
            }
        }

        private fun afterUpdate(update: Update, type: String, listener: Listener?) {
            (listener ?: CommandManager.Handle(bot.telegramClient)).afterUpdate(update, bot.telegramClient)

            if (!bot.options.logUpdates) return
            bot.logger.info("${update.updateId} - $type")
        }
    }
}
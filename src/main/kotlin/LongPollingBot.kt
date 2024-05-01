package io.github.com6235.tgbotter

import org.slf4j.LoggerFactory
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.longpolling.BotSession
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Class for creating long-polling bots using Telegram Bot API
 */
class LongPollingBot(private val options: BotCreationOptions) {
    private val telegramClient: TelegramClient = OkHttpTelegramClient(this.options.token)
    private val application = TelegramBotsLongPollingApplication()
    private val listeners: MutableList<Listener> = mutableListOf()
    internal val logger = LoggerFactory.getLogger(this::class.java)
    private lateinit var botSession: BotSession

    /**
     * This bot's command manager.
     * Commands run first, before any other listener (change with [BotCreationOptions.runCommandsThroughOnMessage])
     */
    val commandManager = CommandManager(this.telegramClient)

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
                logUpdate(update.updateId, "Command")
                if (!bot.options.runCommandsThroughOnMessage) {
                    return
                }
            }
            bot.getListeners().subList(1, bot.getListeners().size).forEach {
                when {
                    update.hasBusinessConnection() -> {
                        it.onBusinessConnection(update.businessConnection, bot.telegramClient)
                        logUpdate(update.updateId, "BusinessConnection")
                    }
                    update.hasBusinessMessage() -> {
                        it.onBusinessMessage(update.businessMessage, bot.telegramClient)
                        logUpdate(update.updateId, "BusinessMessage")
                    }
                    update.hasCallbackQuery() -> {
                        it.onCallbackQuery(update.callbackQuery, bot.telegramClient)
                        logUpdate(update.updateId, "CallbackQuery")
                    }
                    update.hasChannelPost() -> {
                        it.onChannelPost(update.channelPost, bot.telegramClient)
                        logUpdate(update.updateId, "ChannelPost")
                    }
                    update.hasChatJoinRequest() -> {
                        it.onChatJoinRequest(update.chatJoinRequest, bot.telegramClient)
                        logUpdate(update.updateId, "ChatJoinRequest")
                    }
                    update.hasChatMember() -> {
                        it.onChatMember(update.chatMember, bot.telegramClient)
                        logUpdate(update.updateId, "ChatMember")
                    }
                    update.hasChosenInlineQuery() -> {
                        it.onChosenInlineQuery(update.chosenInlineQuery, bot.telegramClient)
                        logUpdate(update.updateId, "ChosenInlineQuery")
                    }
                    update.hasDeletedBusinessMessage() -> {
                        it.onDeletedBusinessMessage(update.deletedBusinessMessages, bot.telegramClient)
                        logUpdate(update.updateId, "DeletedBusinessMessage")
                    }
                    update.hasEditedBusinessMessage() -> {
                        it.onEditedBusinessMessage(update.editedBuinessMessage, bot.telegramClient)
                        logUpdate(update.updateId, "EditedBusinessMessage")
                    }
                    update.hasEditedChannelPost() -> {
                        it.onEditedChannelPost(update.editedChannelPost, bot.telegramClient)
                        logUpdate(update.updateId, "EditedChannelPost")
                    }
                    update.hasEditedMessage() -> {
                        it.onEditedMessage(update.editedMessage, bot.telegramClient)
                        logUpdate(update.updateId, "EditedMessage")
                    }
                    update.hasInlineQuery() -> {
                        it.onInlineQuery(update.inlineQuery, bot.telegramClient)
                        logUpdate(update.updateId, "InlineQuery")
                    }
                    update.hasMessage() -> {
                        it.onMessage(update.message, bot.telegramClient)
                        logUpdate(update.updateId, "Message")
                    }
                    update.hasMyChatMember() -> {
                        it.onMyChatMember(update.myChatMember, bot.telegramClient)
                        logUpdate(update.updateId, "MyChatMember")
                    }
                    update.hasPoll() -> {
                        it.onPoll(update.poll, bot.telegramClient)
                        logUpdate(update.updateId, "Poll")
                    }
                    update.hasPollAnswer() -> {
                        it.onPollAnswer(update.pollAnswer, bot.telegramClient)
                        logUpdate(update.updateId, "PollAnswer")
                    }
                    update.hasPreCheckoutQuery() -> {
                        it.onPreCheckoutQuery(update.preCheckoutQuery, bot.telegramClient)
                        logUpdate(update.updateId, "PreCheckoutQuery")
                    }
                    update.hasShippingQuery() -> {
                        it.onShippingQuery(update.shippingQuery, bot.telegramClient)
                        logUpdate(update.updateId, "ShippingQuery")
                    }
                }
            }
        }

        private fun logUpdate(updateId: Int, type: String) {
            if (!bot.options.logUpdates) return
            bot.logger.info("$updateId - $type")
        }
    }
}
package io.github.com6235.tgbotter

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.longpolling.BotSession
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer
import org.telegram.telegrambots.meta.api.objects.Update
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
    fun addListener(listener: Listener) { this.listeners.add(listener) }
    internal fun getListeners(): MutableList<Listener> {
        val s = mutableListOf<Listener>(); s.addAll(this.listeners)
        return s
    }

    /**
     * Starts the bot session.
     */
    fun start() {
        botSession = application.registerBot(this.options.token, Consumer(this))
        botSession.start()
    }

    /**
     * Stops the bot session
     */
    fun stop() {
        botSession.stop()
        application.unregisterBot(this.options.token)
    }

    private class Consumer(private val bot: LongPollingBot) : LongPollingUpdateConsumer {
        val updatesProcessorExecutor: Executor = Executors.newSingleThreadExecutor()

        override fun consume(updates: List<Update>) {
            updates.forEach {
                updatesProcessorExecutor.execute { consume(it) }
            }
        }

        fun consume(update: Update) {
            if (update.hasMessage() && update.message.hasText() && update.message.text.startsWith("/")) {
                bot.getListeners().first().onMessage(update.message, bot.telegramClient)
                if (!bot.options.runCommandsThroughOnMessage) {
                    return
                }
            }
            bot.getListeners().subList(1, bot.getListeners().size).forEach {
                when {
                    update.hasBusinessConnection() ->  it.onBusinessConnection(update.businessConnection, bot.telegramClient)
                    update.hasBusinessMessage() ->  it.onBusinessMessage(update.businessMessage, bot.telegramClient)
                    update.hasCallbackQuery() ->  it.onCallbackQuery(update.callbackQuery, bot.telegramClient)
                    update.hasChannelPost() ->  it.onChannelPost(update.channelPost, bot.telegramClient)
                    update.hasChatJoinRequest() ->  it.onChatJoinRequest(update.chatJoinRequest, bot.telegramClient)
                    update.hasChatMember() ->  it.onChatMember(update.chatMember, bot.telegramClient)
                    update.hasChosenInlineQuery() ->  it.onChosenInlineQuery(update.chosenInlineQuery, bot.telegramClient)
                    update.hasDeletedBusinessMessage() ->  it.onDeletedBusinessMessage(update.deletedBusinessMessages, bot.telegramClient)
                    update.hasEditedBusinessMessage() ->  it.onEditedBusinessMessage(update.editedBuinessMessage, bot.telegramClient)
                    update.hasEditedChannelPost() ->  it.onEditedChannelPost(update.editedChannelPost, bot.telegramClient)
                    update.hasEditedMessage() ->  it.onEditedMessage(update.editedMessage, bot.telegramClient)
                    update.hasInlineQuery() ->  it.onInlineQuery(update.inlineQuery, bot.telegramClient)
                    update.hasMessage() ->  it.onMessage(update.message, bot.telegramClient)
                    update.hasMyChatMember() ->  it.onMyChatMember(update.myChatMember, bot.telegramClient)
                    update.hasPoll() ->  it.onPoll(update.poll, bot.telegramClient)
                    update.hasPollAnswer() ->  it.onPollAnswer(update.pollAnswer, bot.telegramClient)
                    update.hasPreCheckoutQuery() ->  it.onPreCheckoutQuery(update.preCheckoutQuery, bot.telegramClient)
                    update.hasShippingQuery() ->  it.onShippingQuery(update.shippingQuery, bot.telegramClient)
                }
            }
        }
    }
}
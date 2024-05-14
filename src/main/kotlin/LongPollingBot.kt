package io.github.com6235.tgbotter

import org.telegram.telegrambots.longpolling.BotSession
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer
import org.telegram.telegrambots.meta.api.methods.commands.DeleteMyCommands
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.system.exitProcess

/**
 * Class for creating long-polling bots using Telegram Bot API
 */
class LongPollingBot(private val options: BotCreationOptions) : Bot(options) {
    override val application = TelegramBotsLongPollingApplication()
    private lateinit var botSession: BotSession

    /**
     * Starts the bot session.
     */
    override fun start(autoSetCommands: Boolean, hookStopToShutdown: Boolean, exitOnError: Boolean) {
        try {
            botSession = application.registerBot(this.options.token, Consumer(this))
            setNameAndCommands(hookStopToShutdown, autoSetCommands)
            botSession.start()
            logger.info("Bot started successfully!")
        } catch (e: TelegramApiException) {
            logger.error("There was an error starting the bot: ${e.message}")
            stop()
            if (exitOnError) exitProcess(1)
        }
    }

    /**
     * Stops the bot session
     */
    override fun stop() {
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
        private val updatesProcessorExecutor: Executor = Executors.newCachedThreadPool()

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
package io.github.com6235.tgbotter

import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.ChatJoinRequest
import org.telegram.telegrambots.meta.api.objects.business.BusinessConnection
import org.telegram.telegrambots.meta.api.objects.business.BusinessMessagesDeleted
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberUpdated
import org.telegram.telegrambots.meta.api.objects.inlinequery.ChosenInlineQuery
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.api.objects.payments.PreCheckoutQuery
import org.telegram.telegrambots.meta.api.objects.payments.ShippingQuery
import org.telegram.telegrambots.meta.api.objects.polls.Poll
import org.telegram.telegrambots.meta.api.objects.polls.PollAnswer
import org.telegram.telegrambots.meta.generics.TelegramClient

/**
 * Interface for creating event listeners.
 *
 * After creating your event listener, remember to add them to your [LongPollingBot] using [LongPollingBot.addListener]
 */
interface Listener {
    fun onBusinessConnection(businessConnection: BusinessConnection, telegramClient: TelegramClient) {}
    fun onBusinessMessage(businessMessage: Message, telegramClient: TelegramClient) {}
    fun onCallbackQuery(callbackQuery: CallbackQuery, telegramClient: TelegramClient) {}
    fun onChannelPost(channelPost: Message, telegramClient: TelegramClient) {}
    fun onChatJoinRequest(chatJoinRequest: ChatJoinRequest, telegramClient: TelegramClient) {}
    fun onChatMember(chatMember: ChatMemberUpdated, telegramClient: TelegramClient) {}
    fun onChosenInlineQuery(chosenInlineQuery: ChosenInlineQuery, telegramClient: TelegramClient) {}
    fun onDeletedBusinessMessage(deletedBusinessMessages: BusinessMessagesDeleted, telegramClient: TelegramClient) {}
    fun onEditedBusinessMessage(editedBusinessMessage: Message, telegramClient: TelegramClient) {}
    fun onEditedChannelPost(editedChannelPost: Message, telegramClient: TelegramClient) {}
    fun onEditedMessage(editedMessage: Message, telegramClient: TelegramClient) {}
    fun onInlineQuery(inlineQuery: InlineQuery, telegramClient: TelegramClient) {}
    fun onMessage(message: Message, telegramClient: TelegramClient) {}
    fun onMyChatMember(myChatMember: ChatMemberUpdated, telegramClient: TelegramClient) {}
    fun onPoll(poll: Poll, telegramClient: TelegramClient) {}
    fun onPollAnswer(pollAnswer: PollAnswer, telegramClient: TelegramClient) {}
    fun onPreCheckoutQuery(preCheckoutQuery: PreCheckoutQuery, telegramClient: TelegramClient) {}
    fun onShippingQuery(shippingQuery: ShippingQuery, telegramClient: TelegramClient) {}
}

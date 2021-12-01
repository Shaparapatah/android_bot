package pro.salebot.mobileclient.mvp.messages

import pro.salebot.mobileclient.models.Message
import pro.salebot.mobileclient.models.MessageData
import pro.salebot.mobileclient.mvp.BasePresenterListener
import pro.salebot.mobileclient.mvp.BaseView

interface MessagesPresenter {
    fun loadMessages(idProject: String, idRoom: String, token: String, email: String, page: Int)
    fun sendMessage(
        idProject: String,
        idRoom: String,
        token: String,
        email: String,
        textMessage: String,
        messageId: String?
    )

    fun lockUser(idProject: String, idRoom: String, token: String, email: String)
    fun pauseBot(idProject: String, idRoom: String, token: String, email: String)
}

interface MessagesView : BaseView {
    fun onSuccess(messageData: MessageData, page: Int)
    fun onSuccessSendMessage()
    fun onNewMessage(message: Message)
    fun onSuccessLockUser(isBlocked: Boolean)
    fun onSuccessPauseBot(isPaused: Boolean)
}

interface MessagesIterator {
    fun startLoadMessages(
        idProject: String,
        idRoom: String,
        token: String,
        email: String,
        page: Int
    )

    fun sendMessage(
        idProject: String,
        idRoom: String,
        token: String,
        email: String,
        textMessage: String,
        messageId: String?
    )

    fun lockUser(idProject: String, idRoom: String, token: String, email: String)
    fun pauseBot(idProject: String, idRoom: String, token: String, email: String)
}

interface MessagesPresenterListener : BasePresenterListener {
    fun onSuccess(messageData: MessageData, page: Int)
    fun onSuccessSendMessage()
    fun onNewMessage(message: Message)
    fun onSuccessLockUser(isBlocked: Boolean)
    fun onSuccessPauseBot(isPaused: Boolean)
}
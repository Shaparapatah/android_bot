package pro.salebot.mobileclient.mvp.messages

import pro.salebot.mobileclient.models.Message
import pro.salebot.mobileclient.models.MessageData

class MessagesPresenterImpl(
    private var messagesView: MessagesView?
) : MessagesPresenter, MessagesPresenterListener {

    private val messagesIteratorImpl = MessagesIteratorImpl(this)

    override fun loadMessages(
        idProject: String,
        idRoom: String,
        token: String,
        email: String,
        page: Int
    ) {
        messagesIteratorImpl.startLoadMessages(idProject, idRoom, token, email, page)
    }

    override fun sendMessage(
        idProject: String,
        idRoom: String,
        token: String,
        email: String,
        textMessage: String,
        messageId: String?
    ) {
        messagesIteratorImpl.sendMessage(idProject, idRoom, token, email, textMessage, messageId)
    }

    override fun lockUser(idProject: String, idRoom: String, token: String, email: String) {
        messagesIteratorImpl.lockUser(
            idProject,
            idRoom,
            token,
            email
        )
    }

    override fun pauseBot(idProject: String, idRoom: String, token: String, email: String) {
        messagesIteratorImpl.pauseBot(
            idProject,
            idRoom,
            token,
            email
        )
    }

    override fun onSuccess(messageData: MessageData, page: Int) {
        messagesView?.onSuccess(messageData, page)
    }

    override fun onSuccessSendMessage() {
        messagesView?.onSuccessSendMessage()
    }

    override fun onNewMessage(message: Message) {
        messagesView?.onNewMessage(message)
    }

    override fun onSuccessLockUser(isBlocked: Boolean) {
        messagesView?.onSuccessLockUser(isBlocked)
    }

    override fun onSuccessPauseBot(isPaused: Boolean) {
        messagesView?.onSuccessPauseBot(isPaused)
    }

    override fun onFailed(errorMess: String) {
        messagesView?.onFailed(errorMess)
    }

    fun cancel() {
        messagesView = null
    }

}
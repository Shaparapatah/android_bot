package pro.salebot.mobileclient.mvp.messages

import pro.salebot.mobileclient.api.RequestServer
import pro.salebot.mobileclient.api.models.Request
import pro.salebot.mobileclient.models.BotStatus
import pro.salebot.mobileclient.models.ClientStatus
import pro.salebot.mobileclient.models.MessageData
import pro.salebot.mobileclient.utils.processingFailure
import pro.salebot.mobileclient.utils.processingResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MessagesIteratorImpl(
    private val messagesPresenterListener: MessagesPresenterListener?
) : MessagesIterator {

    private val requestServer: RequestServer by lazy {
        RequestServer.create()
    }

    override fun startLoadMessages(
        idProject: String,
        idRoom: String,
        token: String,
        email: String,
        page: Int
    ) {

        val call = requestServer.getMessages(
            idProject,
            idRoom,
            token,
            email,
            page
        )

        call.enqueue(object : Callback<MessageData> {
            override fun onResponse(call: Call<MessageData>?, response: Response<MessageData>?) {
                response.processingResponse(messagesPresenterListener) {
                    messagesPresenterListener?.onSuccess(it, page)
                }
            }

            override fun onFailure(call: Call<MessageData>, t: Throwable) {
                t.processingFailure(messagesPresenterListener)
            }
        })
    }

    override fun lockUser(idProject: String, idRoom: String, token: String, email: String) {

        val call = requestServer.blockClient(idProject, idRoom, token, email)

        call.enqueue(object : Callback<ClientStatus> {
            override fun onResponse(call: Call<ClientStatus>, response: Response<ClientStatus>) {
                response.processingResponse(messagesPresenterListener) {
                    messagesPresenterListener?.onSuccessLockUser(it.isBlocked == 1)
                }
            }

            override fun onFailure(call: Call<ClientStatus>, t: Throwable) {
                t.processingFailure(messagesPresenterListener)
            }
        })
    }

    override fun pauseBot(idProject: String, idRoom: String, token: String, email: String) {
        val call = requestServer.pauseBot(idProject, idRoom, token, email)

        call.enqueue(object : Callback<BotStatus> {
            override fun onResponse(call: Call<BotStatus>, response: Response<BotStatus>) {
                response.processingResponse(messagesPresenterListener) {
                    messagesPresenterListener?.onSuccessPauseBot(it.isPaused == 1)
                }
            }

            override fun onFailure(call: Call<BotStatus>, t: Throwable) {
                t.processingFailure(messagesPresenterListener)
            }
        })
    }

    override fun sendMessage(
        idProject: String,
        idRoom: String,
        token: String,
        email: String,
        textMessage: String,
        messageId: String?
    ) {

        if (textMessage.trim().isEmpty()) {
            messagesPresenterListener?.onFailed("error")
            return
        }

        val sendMessageCall =
            requestServer.sendMessage(idProject, idRoom, token, email, textMessage, messageId)
        sendMessageCall.enqueue(object : Callback<Request> {
            override fun onResponse(call: Call<Request>?, response: Response<Request>?) {
                response.processingResponse(messagesPresenterListener) {
                    if (it.success) {
                        messagesPresenterListener?.onSuccessSendMessage()
                    } else {
                        messagesPresenterListener?.onFailed("error")
                    }
                }
            }

            override fun onFailure(call: Call<Request>?, t: Throwable?) {
                t.processingFailure(messagesPresenterListener)
            }
        })
    }


}
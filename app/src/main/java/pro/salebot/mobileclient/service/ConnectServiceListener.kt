package pro.salebot.mobileclient.service

import pro.salebot.mobileclient.models.Message

interface ConnectServiceListener {
    fun onNewMessageService(message: Message)
}
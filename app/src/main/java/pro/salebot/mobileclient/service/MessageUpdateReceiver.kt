package pro.salebot.mobileclient.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import pro.salebot.mobileclient.models.Message
import java.text.SimpleDateFormat
import java.util.*

const val ACTION_UPDATE_MESSAGES = "ACTION_UPDATE_MESSAGES"

private const val FORMAT_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"

class MessagesUpdateReceiver(private val listener: MessagesUpdateListener) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            val incomeMessage = it.getStringExtra("type") == "incoming_message"
                    || it.getStringExtra("type") == "message_from_client"

            listener.onNewMessageUpdate(
                Message(
                    it.getStringExtra("id") ?: "-1",
                    incomeMessage,
                    false,
                    it.getStringExtra("message").orEmpty(),
                    "",
                    it.getIntExtra("client_id", -1),
                    it.getIntExtra("project_id", -1),
                    SimpleDateFormat(
                        FORMAT_DATE_TIME,
                        Locale.getDefault()
                    ).parse(it.getStringExtra("date")!!),
                    SimpleDateFormat(
                        FORMAT_DATE_TIME,
                        Locale.getDefault()
                    ).parse(it.getStringExtra("date")!!),
                    it.getIntExtra("delivered", -1) == 1
                )
            )
        }
    }
}

interface MessagesUpdateListener {
    fun onNewMessageUpdate(message: Message)
}
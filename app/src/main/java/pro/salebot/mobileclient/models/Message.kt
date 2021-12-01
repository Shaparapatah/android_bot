package pro.salebot.mobileclient.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class MessageData(
    @SerializedName("is_blocked") val isBlocked: Boolean,
    @SerializedName("is_paused") val isPaused: Boolean,
    @SerializedName("messages") val messages: List<Message>
)

data class ClientStatus(
    @SerializedName("is_blocked") val isBlocked: Int
)

data class BotStatus(
    @SerializedName("is_paused") val isPaused: Int
)

data class Message(
    var id: String? = "",
    var client_replica: Boolean? = false,
    var answered: Boolean? = false,
    var text: String? = "",
    var attachments: String? = "",
    var client_id: Int?,
    var project_id: Int?,
    var created_at: Date?,
    var updated_at: Date?,
    var delivered: Boolean = true
) : Serializable {
    fun getAttachments() = if (attachments.isNullOrEmpty()) {
        listOf()
    } else {
        Gson().fromJson(this.attachments, Array<String>::class.java).toList()
    }
}
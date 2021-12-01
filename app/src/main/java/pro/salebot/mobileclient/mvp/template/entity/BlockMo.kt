package pro.salebot.mobileclient.mvp.template.entity

import com.google.gson.annotations.SerializedName

data class BlockMo(
    @SerializedName("id") val id: Int,
    @SerializedName("answer") val answer: String,
    @SerializedName("message_type") val messageType: MessageType?
)

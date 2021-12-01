package pro.salebot.mobileclient.mvp.template.entity

import com.google.gson.annotations.SerializedName

data class MessagesBlocksMo(
    @SerializedName("blocks") val blocks: List<BlockMo>?
)

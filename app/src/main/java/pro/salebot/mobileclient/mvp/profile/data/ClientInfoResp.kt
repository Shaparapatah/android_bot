package pro.salebot.mobileclient.mvp.profile.data

import com.google.gson.annotations.SerializedName
import java.util.*

data class ClientInfoResp(
    @SerializedName("last_update") val lastUpdate: Date,
    @SerializedName("user_info") val clientInfoList: Map<String, String>,
    @SerializedName("order_fields") val orderFields: Map<String, String>,
    @SerializedName("user_fields") val userFields: Map<String, String>,
    @SerializedName("is_my_client") val isMyClient: Boolean,
    @SerializedName("responsible_id") val responsibleId: Int?
)
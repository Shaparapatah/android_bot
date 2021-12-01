package pro.salebot.mobileclient.mvp.profile.data

data class ProfileInfoData(
    val imageUrl: String,
    val name: String,
    val clientId: String,
    var lastUpdate: String? = null,
    var responsibleId: Int? = null,
    var isMyClient: Boolean = false
)

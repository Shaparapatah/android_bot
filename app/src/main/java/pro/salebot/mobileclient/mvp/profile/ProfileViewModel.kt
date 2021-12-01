package pro.salebot.mobileclient.mvp.profile

import pro.salebot.mobileclient.mvp.profile.data.ProfileInfoData
import pro.salebot.mobileclient.mvp.profile.data.VariablesMo

data class ProfileViewModel(
    val profileInfoData: ProfileInfoData,
//    val imageUrl: String,
//    val name: String,
//    val clientId: String,
    val projectId: String,
    var variablesList: MutableList<VariablesMo> = mutableListOf(),
    var orderFields: MutableList<VariablesMo> = mutableListOf(),
    var userFields: MutableList<VariablesMo> = mutableListOf(),
    var clientUrlList: List<String> = emptyList()
//    var isMyClient: Boolean = false
)

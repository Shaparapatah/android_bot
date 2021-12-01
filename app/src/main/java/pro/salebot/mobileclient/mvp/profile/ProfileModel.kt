package pro.salebot.mobileclient.mvp.profile

import pro.salebot.mobileclient.mvp.profile.data.ClientInfoResp

interface ProfileModel {
    fun loadUserInfo(
        projectId: String,
        clientId: String,
        cb: (ClientInfoResp) -> Unit
    )

    fun sendOrderVar(
        projectId: String,
        clientId: String,
        variable: String,
        value: String,
        cb: (Boolean) -> Unit
    )

    fun sendUserVar(
        projectId: String,
        clientId: String,
        variable: String,
        value: String,
        cb: (Boolean) -> Unit
    )

    fun deleteOrderVar(
        projectId: String,
        clientId: String,
        variable: String,
        cb: (Boolean) -> Unit
    )

    fun deleteUserVar(
        projectId: String,
        clientId: String,
        variable: String,
        cb: (Boolean) -> Unit
    )

    fun takeClient(
        projectId: String,
        clientId: String,
        cb: (Boolean) -> Unit
    )

    fun refuseClient(
        projectId: String,
        clientId: String,
        cb: (Boolean) -> Unit
    )

}
package pro.salebot.mobileclient.mvp.profile

import okhttp3.ResponseBody
import pro.salebot.mobileclient.api.RequestServer
import pro.salebot.mobileclient.database.DataBaseParams
import pro.salebot.mobileclient.mvp.profile.data.ClientInfoResp
import pro.salebot.mobileclient.utils.processingFailure
import pro.salebot.mobileclient.utils.processingResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileModelImpl(
    private val dataBaseParams: DataBaseParams
) : ProfileModel {

    val login by lazy {
        dataBaseParams.getKey(DataBaseParams.KEY_LOGIN) ?: ""
    }
    val token by lazy {
        dataBaseParams.getKey(DataBaseParams.KEY_TOKEN) ?: ""
    }

    private val requestServer: RequestServer by lazy {
        RequestServer.create()
    }

    override fun loadUserInfo(
        projectId: String,
        clientId: String,
        cb: (ClientInfoResp) -> Unit
    ) {
        requestServer.getProfileInfo(projectId, clientId, token, login).enqueue(object :
            Callback<ClientInfoResp> {
            override fun onResponse(
                call: Call<ClientInfoResp>?,
                response: Response<ClientInfoResp>?
            ) {
                response.processingResponse(null) {
                    cb.invoke(it)
                }
            }

            override fun onFailure(call: Call<ClientInfoResp>, t: Throwable) {
                t.processingFailure(null)
            }
        })

    }

    override fun sendOrderVar(
        projectId: String,
        clientId: String,
        variable: String,
        value: String,
        cb: (Boolean) -> Unit
    ) {
        requestServer.changeOrderVar(
            token,
            login,
            projectId,
            clientId,
            variable,
            value
        ).enqueue(object :
            Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                cb.invoke(false)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                cb.invoke(response.isSuccessful)
            }

        })
    }

    override fun sendUserVar(
        projectId: String,
        clientId: String,
        variable: String,
        value: String,
        cb: (Boolean) -> Unit
    ) {
        requestServer.changeUserVar(
            token,
            login,
            projectId,
            clientId,
            variable,
            value
        ).enqueue(object :
            Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                cb.invoke(false)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                cb.invoke(response.isSuccessful)
            }

        })
    }

    override fun deleteOrderVar(
        projectId: String,
        clientId: String,
        variable: String,
        cb: (Boolean) -> Unit
    ) {
        requestServer.deleteOrderVar(
            projectId,
            clientId,
            token,
            login,
            variable
        ).enqueue(object :
            Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                cb.invoke(false)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                cb.invoke(response.isSuccessful)
            }

        })
    }

    override fun deleteUserVar(
        projectId: String,
        clientId: String,
        variable: String,
        cb: (Boolean) -> Unit
    ) {
        requestServer.deleteUserVar(
            projectId,
            clientId,
            token,
            login,
            variable
        ).enqueue(object :
            Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                cb.invoke(false)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                cb.invoke(response.isSuccessful)
            }

        })
    }

    override fun takeClient(projectId: String, clientId: String, cb: (Boolean) -> Unit) {
        requestServer.takeClient(
            projectId,
            clientId,
            token,
            login
        ).enqueue(object :
            Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                cb.invoke(false)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                cb.invoke(response.isSuccessful)
            }

        })
    }

    override fun refuseClient(projectId: String, clientId: String, cb: (Boolean) -> Unit) {
        requestServer.refuseClient(
            projectId,
            clientId,
            token,
            login
        ).enqueue(object :
            Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                cb.invoke(false)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                cb.invoke(response.isSuccessful)
            }

        })
    }
}
package pro.salebot.mobileclient.mvp.template

import pro.salebot.mobileclient.api.RequestServer
import pro.salebot.mobileclient.database.DataBaseParams
import pro.salebot.mobileclient.mvp.template.entity.MessagesBlocksMo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TemplateModelImpl(
    private val dataBaseParams: DataBaseParams
) : TemplateModel {

    val login by lazy {
        dataBaseParams.getKey(DataBaseParams.KEY_LOGIN) ?: ""
    }
    val token by lazy {
        dataBaseParams.getKey(DataBaseParams.KEY_TOKEN) ?: ""
    }

    private val requestServer: RequestServer by lazy {
        RequestServer.create()
    }

    override fun getMessageBlocks(
        projectId: String,
        type: String?,
        search: String?,
        cb: (MessagesBlocksMo?) -> Unit
    ) {
        requestServer.getMessagesBlocks(
            projectId,
            token,
            login,
            type,
            search
        ).enqueue(object : Callback<MessagesBlocksMo> {
            override fun onResponse(
                call: Call<MessagesBlocksMo>,
                response: Response<MessagesBlocksMo>
            ) {
                cb.invoke(response.body())
            }

            override fun onFailure(call: Call<MessagesBlocksMo>, t: Throwable) {
                cb.invoke(null)
            }
        })

    }
}
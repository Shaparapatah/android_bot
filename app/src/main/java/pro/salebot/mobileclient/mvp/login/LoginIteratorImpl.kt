package pro.salebot.mobileclient.mvp.login

import android.os.Handler
import org.json.JSONException
import org.json.JSONObject
import pro.salebot.mobileclient.api.RequestServer
import pro.salebot.mobileclient.api.models.User
import pro.salebot.mobileclient.api.models.UserStructure
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginIteratorImpl(val loginView: LoginView?): LoginIterator {

    private val requestServer: RequestServer by lazy {
        RequestServer.create()
    }

    override fun enterUser(login: String, pass: String) {

        var call = requestServer.enter(
            UserStructure(User(login, pass))
        )

        call.enqueue(object : Callback<pro.salebot.mobileclient.models.User> {
            override fun onResponse(
                call: Call<pro.salebot.mobileclient.models.User>?,
                response: Response<pro.salebot.mobileclient.models.User>?
            ) {
                response?.let {
                    if (response.isSuccessful && it.body() != null) {
                        loginView?.onSuccess(it.body()!!.authentication_token!!)
                    } else {
                        try {
                            val jObjError = JSONObject(it.errorBody()!!.string())
                            loginView?.onFailed(jObjError.getString("error"))
                        } catch (e: JSONException) {
                            loginView?.onFailed("unknown error")
                        }
                    }
                }
            }

            override fun onFailure(call: Call<pro.salebot.mobileclient.models.User>?, t: Throwable?) {
                loginView?.let {
                    if (t != null) {
                        it.onFailed(t.localizedMessage)
                    } else {
                        it.onFailed("error")
                    }
                }
            }
        })

    }

    override fun checkAuthUser(login: String, token: String) {
        Handler().postDelayed({
            loginView?.onSuccess(token)
        }, 1000)
    }

}
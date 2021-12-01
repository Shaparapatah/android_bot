package pro.salebot.mobileclient.service

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.hosopy.actioncable.ActionCable
import com.hosopy.actioncable.Channel
import com.hosopy.actioncable.Consumer
import pro.salebot.mobileclient.models.Message
import java.net.URI
import java.net.URISyntaxException
import java.text.SimpleDateFormat
import java.util.*

private const val FORMAT_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"

class ConnectForAppService : Service() {

    private val mLocalbinder = MyBinder()

    //    private var consumer: Consumer? = null
    private var token = ""
    private var email = ""

    private var listener: ConnectServiceListener? = null

    companion object {
        private var consumer: Consumer? = null
        const val EXTRA_EMAIL = "EXTRA_EMAIL"
        const val EXTRA_TOKEN = "EXTRA_TOKEN"
        private var connectForAppService: ConnectForAppService? = null
        private var connectServiceListener: ConnectServiceListener? = null
        private val serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
                (p1 as MyBinder).getService()?.let {
                    connectForAppService = it
                    connectForAppService?.listener =
                        connectServiceListener
                }
            }

            override fun onServiceDisconnected(p0: ComponentName?) {
                consumer?.disconnect()
                connectForAppService?.listener = null
            }
        }

        fun startWithListener(context: Context?, email: String, token: String, csl: ConnectServiceListener) {
            Intent(context, ConnectForAppService::class.java).apply {
                context?.let {
                    putExtra(EXTRA_EMAIL, email)
                    putExtra(EXTRA_TOKEN, token)
                    connectServiceListener = csl
                    Log.d("qweqweqwe", "bindService")
                    it.bindService(this,
                        serviceConnection, BIND_AUTO_CREATE)
                }

            }
        }

        fun stop(context: Context?) = Intent(context, ConnectForAppService::class.java).apply {
            context?.let {
                consumer?.disconnect()
                it.stopService(this)
                connectForAppService?.let {
                    Log.d("qweqweqwe", "unbindService")
                    try {
                        context.unbindService(serviceConnection)
                    } catch (e: IllegalArgumentException) {
                    }
                }
            }

        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        connectServer(intent)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        connectServer(intent)
        Log.d("qweqweqwe", "onBind")
        return mLocalbinder
    }

    private fun connectServer(intent: Intent?) {
//        NotificationUtils.showAlwaysNotification(
//            this
//        )

        intent?.let {
            email = it.getStringExtra(EXTRA_EMAIL)!!
            token = it.getStringExtra(EXTRA_TOKEN)!!
        }

        Log.d("qweqweqwe", "check data")

        if (email.isEmpty() || token.isEmpty()) return

        Log.d("qweqweqwe", "attempt connection")

        val uri = convertFromString(email, token)

        consumer = ActionCable.createConsumer(uri)

        // 2. Create subscription
        val appearanceChannel = Channel("NotifierChannel")
        val subscription = consumer!!.subscriptions.create(appearanceChannel)

        subscription
            .onConnected {
                // Called when the subscription has been successfully completed
//                Log.i(TAG, "ConnectedCallback: ")
//                runOnUiThread(Runnable { Toast.makeText(context, "Подключено.", Toast.LENGTH_SHORT).show() })

//                attempt = 0
                Log.d("qweqweqwe", "Called when the subscription has been successfully completed")
            }
            .onRejected {
                // Called when the subscription is rejected by the server
//                Log.i(TAG, "RejectedCallback: ")
//                runOnUiThread(Runnable { Toast.makeText(context, "Соединение отклонено.", Toast.LENGTH_SHORT).show() })
                Log.d("qweqweqwe", "Called when the subscription has been successfully completed")

            }.onReceived { data ->
                // Called when the subscription receives data from the server
//                Log.i(TAG, "ReceivedCallback: $data")

                try {
                    val parser = JsonParser()

                    val o = parser.parse(data.asJsonObject.get("message").asString).asJsonObject
                    val incomeMessage =
                        o.get("type").asString == "incoming_message" || o.get("type").asString == "message_from_client"
                    listener?.onNewMessageService(
                        Message(
                            o.get("id")?.asString ?: "-1",
                            incomeMessage,
                            false,
                            o.get("message")?.asString ?: "",
                            "",
                            o.get("client_id").asInt,
                            o.get("project_id").asInt,
                            SimpleDateFormat(
                                FORMAT_DATE_TIME,
                                Locale.getDefault()
                            ).parse(o.get("date").asString),
                            SimpleDateFormat(
                                FORMAT_DATE_TIME,
                                Locale.getDefault()
                            ).parse(o.get("date").asString),
                            o.get("delivered").asInt == 1
                        )
                    )

                    Log.d("qweqweqwe", Gson().toJson(data))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.onDisconnected {
                // Called when the subscription has been closed
                Log.d("qweqweqwe", "Called when the subscription has been closed")
            }.onFailed {
                // Called when the subscription encounters any error
                Handler(Looper.getMainLooper()).postDelayed({
                    consumer!!.connect()
                    Log.d("qweqweqwe", "reconnect for app")
                }, 10000)
                Log.d("qweqweqwe", "Called when the subscription encounters any error")
            }

        // 3. Establish connection
        consumer!!.connect()
    }

    private fun convertFromString(email: String, token: String): URI? {
        val uri: URI

        try {
            uri = URI("wss://salebot.pro/cable?user_email=$email&user_token=$token")
        } catch (e: URISyntaxException) {
            e.printStackTrace()
            return null
        }

        return uri
    }

    inner class MyBinder : Binder() {
        fun getService(): ConnectForAppService? = this@ConnectForAppService
    }
}
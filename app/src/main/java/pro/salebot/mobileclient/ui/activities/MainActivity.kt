package pro.salebot.mobileclient.ui.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.JsonParser
import pro.salebot.mobileclient.Constants
import pro.salebot.mobileclient.R
import pro.salebot.mobileclient.database.DataBaseParams
import pro.salebot.mobileclient.mvp.login.LoginFragment
import pro.salebot.mobileclient.mvp.messages.MessagesActivity
import pro.salebot.mobileclient.mvp.rooms.RoomsFragment

class MainActivity : AppCompatActivity(), MainContract {

    private var typeActiveFragment: Int = MainType.LOGIN.ordinal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent?.let {
            checkNotificationIntent(it)
        }

        FirebaseAnalytics.getInstance(this);

        setContentView(R.layout.activity_main)
        val dataBaseParams = DataBaseParams(this)
        val login: String = dataBaseParams.getKey(DataBaseParams.KEY_LOGIN) ?: ""
        val token: String = dataBaseParams.getKey(DataBaseParams.KEY_TOKEN) ?: ""
        if (login.isNotEmpty() && token.isNotEmpty()) {
            showRooms()
        } else {
            showLogin()
        }
        dataBaseParams.close()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            if (it.hasExtra(Constants.EXTRA_ID_PROJECT)) {
                showRooms()
            }
            checkNotificationIntent(it)
        }

    }

    private fun checkNotificationIntent(intent: Intent) {
        if (intent.hasExtra(Constants.EXTRA_PUSH_MESSAGE)) {
            val parser = JsonParser()
            val o = parser.parse(intent.getStringExtra("message")).asJsonObject

            showMessages(
                o.get("project_id").asString,
                o.get("client_id").asString,
                o.get("name")?.asString.orEmpty(),
                o.get("avatar")?.asString.orEmpty(),
                o.get("client_type")?.asInt ?: Constants.DEFAULT_ID
            )
            finish()
        }
    }

    override fun showLogin() {
        typeActiveFragment = MainType.LOGIN.ordinal
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.content, LoginFragment.newInstance(this))
        fragmentTransaction.commitAllowingStateLoss()
    }

    override fun showRooms() {
        typeActiveFragment = MainType.ROOMS.ordinal
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        if (intent != null) {
            fragmentTransaction.replace(
                R.id.content, RoomsFragment.newInstance(
                    this,
                    intent.getIntExtra(Constants.EXTRA_ID_PROJECT, Constants.DEFAULT_ID),
                    intent.getIntExtra(Constants.EXTRA_ID_ROOM, Constants.DEFAULT_ID)
                )
            )
            intent = null
        } else {
            fragmentTransaction.replace(R.id.content, RoomsFragment.newInstance(this))
        }
        fragmentTransaction.commitAllowingStateLoss()
    }

    override fun showMessages(
        idProject: String,
        idRoom: String,
        name: String,
        imageUrl: String,
        clientType: Int
    ) {
        MessagesActivity.start(
            this,
            idProject,
            idRoom,
            name,
            imageUrl,
            clientType,
            5
        )
    }

    private fun showDialog() {
        AlertDialog.Builder(this, R.style.AlertDialogStyle)
            .setTitle(getString(R.string.close_app))
            .setPositiveButton(R.string.yes) { _, _ -> finish() }
            .setNegativeButton(R.string.no) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?) = when {
        event?.keyCode == KeyEvent.KEYCODE_BACK -> when (typeActiveFragment) {
            MainType.LOGIN.ordinal -> super.onKeyLongPress(keyCode, event)
            MainType.ROOMS.ordinal -> {
                showDialog(); true
            }
            MainType.MESSAGES.ordinal -> {
                showRooms(); true
            }
            else -> super.onKeyLongPress(keyCode, event)
        }
        else -> super.onKeyLongPress(keyCode, event)
    }

}

interface MainContract {
    fun showLogin()
    fun showRooms()
    fun showMessages(
        idProject: String,
        idRoom: String,
        name: String,
        imageUrl: String,
        clientType: Int
    )
}

enum class MainType {
    LOGIN,
    ROOMS,
    MESSAGES
}
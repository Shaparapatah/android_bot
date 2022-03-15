package pro.salebot.mobileclient.mvp.messages

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_messages.*
import kotlinx.android.synthetic.main.view_error.*
import pro.salebot.mobileclient.Constants
import pro.salebot.mobileclient.R
import pro.salebot.mobileclient.database.DataBaseParams
import pro.salebot.mobileclient.models.Message
import pro.salebot.mobileclient.models.MessageData
import pro.salebot.mobileclient.mvp.profile.ProfileActivity
import pro.salebot.mobileclient.mvp.template.TemplateActivity
import pro.salebot.mobileclient.rv.messages.MessagesAdapter
import pro.salebot.mobileclient.service.ConnectForAppService
import pro.salebot.mobileclient.service.ConnectServiceListener
import pro.salebot.mobileclient.ui.activities.GalleryActivity
import pro.salebot.mobileclient.ui.activities.MainActivity
import pro.salebot.mobileclient.utils.NotificationUtils
import pro.salebot.mobileclient.utils.PlayerController
import pro.salebot.mobileclient.utils.isVisible
import java.util.*

private const val REQUEST_CODE_TEMPLATE = 1

class MessagesActivity : AppCompatActivity(), MessagesView,
    ConnectServiceListener {

    private val playerController = PlayerController(this)

    private val messagePresenterImpl = MessagesPresenterImpl(this)
    private var messagesAdapter =
        MessagesAdapter(mutableListOf(), mutableMapOf(), "", {}, { _, _ -> }, { _, _ -> })
    private lateinit var idRoom: String
    private lateinit var idProject: String
    private lateinit var login: String
    private lateinit var token: String
    private var messages = mutableListOf<Message>()
    private var isBlocked: Boolean = false
    private var isPaused: Boolean = false
    private var messageId: String? = null

    companion object {

        fun start(
            ctx: Context,
            idProject: String,
            idRoom: String,
            name: String,
            image: String,
            clientType: Int,
            usersCounts: Int
        ) {
            val intent = Intent(ctx, MessagesActivity::class.java).apply {
                putExtra(Constants.EXTRA_ID_PROJECT, idProject)
                putExtra(Constants.EXTRA_ID_ROOM, idRoom)
                putExtra(Constants.EXTRA_NAME_ROOM, name)
                putExtra(Constants.EXTRA_IMAGE_ROOM, image)
                putExtra(Constants.EXTRA_CLIENT_TYPE, clientType)
                putExtra(Constants.EXTRA_USERS_COUNTS, usersCounts)
            }
            try {
                ctx.startActivity(intent)
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        val appLinkAction = intent.action
        val appLinkData: Uri? = intent.data

        init()
        if (intent != null) {
            appLinkData?.lastPathSegment
            Uri.parse("salebot.pro/projects")
                .buildUpon()
                .build().also {
                    idRoom = intent.getStringExtra(Constants.EXTRA_ID_ROOM)!!
                    idProject = intent.getStringExtra(Constants.EXTRA_ID_PROJECT)!!
                    messagePresenterImpl.loadMessages(
                        idProject,
                        idRoom,
                        token,
                        login,
                        1
                    )
                }
            messagesAdapter.notifyDataSetChanged()
            Glide.with(this)
                .load(intent.getStringExtra(Constants.EXTRA_IMAGE_ROOM))
                .error(R.drawable.ic_no_avatar)
                .into(imageUserView)
            toolbarView.title = intent.getStringExtra(Constants.EXTRA_NAME_ROOM)
            toolbarView.setSubtitleTextColor(resources.getColor(R.color.subtitle))
            toolbarView.subtitle = intent.getIntExtra(Constants.EXTRA_CLIENT_TYPE, -1).takeIf {
                it != -1 && Constants.ClientType.getTypeResString(it) != -1
            }?.let {
                getString(Constants.ClientType.getTypeResString(it))
            } ?: ""
        } else {
            Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show()
            finish()
        }

        ivMore.setOnClickListener {
            it.showContextMenu()
        }
        ivTemplate.setOnClickListener {
            startActivityForResult(
                TemplateActivity.getIntent(this, idProject),
                REQUEST_CODE_TEMPLATE
            )
        }
    }

    private fun setPopupMenu(
        isBlocked: Boolean,
        isPaused: Boolean
    ) {
        ivMore.setOnCreateContextMenuListener { menu, v, menuInfo ->
            val lockText = getString(
                if (isBlocked)
                    R.string.messages_unlock
                else
                    R.string.messages_lock
            )
            menu.add(lockText).apply {
                this.setIcon(R.drawable.ic_lock)
                this.setOnMenuItemClickListener {
                    messagePresenterImpl.lockUser(idProject, idRoom, token, login)
                    true
                }
            }

            val runText = getString(
                if (isPaused)
                    R.string.messages_play_bot
                else
                    R.string.messages_pause_bot
            )
            menu.add(runText).apply {
                this.setIcon(R.drawable.ic_play_bot)
                this.setOnMenuItemClickListener {
                    messagePresenterImpl.pauseBot(idProject, idRoom, token, login)
                    true
                }
            }
            menu.add(R.string.messages_client_info).apply {
                this.setIcon(R.drawable.ic_info)
                this.setOnMenuItemClickListener {
                    startProfileActivity()
                    true
                }
            }
        }
    }

    private fun init() {

        toolbarView.setNavigationIcon(R.drawable.ic_back)
        toolbarView.setNavigationOnClickListener {
            startMainActivity()
        }

        val dataBaseParams = DataBaseParams(this)
        login = dataBaseParams.getKey(DataBaseParams.KEY_LOGIN) ?: ""
        token = dataBaseParams.getKey(DataBaseParams.KEY_TOKEN) ?: ""

        errorView.visibility = View.GONE
        progressBarView.visibility = View.VISIBLE
        progressBarMessageView.visibility = View.GONE
        sendView.isEnabled = false
        sendView.setOnClickListener {
            if (!messageView.text.isNullOrEmpty()) {
                it.visibility = View.GONE
                progressBarMessageView.visibility = View.VISIBLE
                messagePresenterImpl.sendMessage(
                    idProject,
                    idRoom,
                    token,
                    login,
                    messageView.text.toString(),
                    messageId
                )
                val dateNow = Date(System.currentTimeMillis())
                messagesAdapter.addSendMessage(
                    Message(
                        "",
                        client_replica = false,
                        answered = true,
                        text = messageView.text.toString(),
                        attachments = "",
                        client_id = idRoom.toInt(),
                        project_id = null,
                        created_at = dateNow,
                        updated_at = dateNow
                    )
                )
                recyclerView.scrollToPosition(0)
            }
        }
        imageUserView.setOnClickListener {
            startProfileActivity()
        }
        btnResetTemplate.setOnClickListener {
            messageView.text.clear()
            llTemplate.isVisible = false
            messageId = null
        }
    }

    private fun startProfileActivity() {
        startActivity(
            ProfileActivity.getIntent(
                this,
                idRoom,
                idProject,
                intent.getStringExtra(Constants.EXTRA_NAME_ROOM).orEmpty(),
                intent.getStringExtra(Constants.EXTRA_IMAGE_ROOM)
            )
        )
    }

    override fun onBackPressed() {
        startMainActivity()
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            if (idProject != DataBaseParams(this@MessagesActivity).getKey(DataBaseParams.KEY_ID_PROJECT)) {
                DataBaseParams(this@MessagesActivity).setKey(
                    DataBaseParams.KEY_ID_PROJECT,
                    idProject
                )
                putExtra(Constants.EXTRA_ID_PROJECT, Constants.DEFAULT_ID)
            }
        })
        finish()
    }

    override fun onStart() {
        super.onStart()

        NotificationUtils.cancel(this, idRoom.toInt())

        val dataBaseParams = DataBaseParams(this)
        val login: String = dataBaseParams.getKey(DataBaseParams.KEY_LOGIN) ?: ""
        val token: String = dataBaseParams.getKey(DataBaseParams.KEY_TOKEN) ?: ""
        if (!login.isEmpty()) {
            if (!token.isEmpty()) {
                Handler().postDelayed({
                    ConnectForAppService.startWithListener(this, login, token, this)
                }, 1000)
            }
        }
    }

    override fun onNewMessageService(message: Message) {
        if (message.client_id == idRoom.toInt()) {
            Handler(Looper.getMainLooper()).postDelayed({
                if (messages.isEmpty() || !message.id.equals(messages[0].id)) {
                    messagesAdapter.add(message)
                    recyclerView.scrollToPosition(0)
                } else if (message.id.equals(messages[0].id)
                    && message.text == messages[0].text
                ) {
                    messagesAdapter.update(message)
                }
                NotificationUtils.cancel(this@MessagesActivity, message.client_id!!)
            }, 1000)
        }
    }

    override fun onStop() {
        super.onStop()
        messagesAdapter.voiceViews.values.forEach {
            it.getPlayerController().pause()
        }
        ConnectForAppService.stop(this)
    }

    override fun onSuccess(messageData: MessageData, page: Int) {
        val messages = messageData.messages
        this.isBlocked = messageData.isBlocked
        this.isPaused = messageData.isPaused
        setPopupMenu(
            messageData.isBlocked,
            messageData.isPaused
        )
        progressBarView.visibility = View.GONE
        sendView.isEnabled = true
        if (messages.isEmpty() && this.messages.isEmpty()) {
            onFailed(getString(R.string.no_messages))
        } else if (page == 1) {
            this.messages.clear()
            this.messages.addAll(messages)
            messagesAdapter = MessagesAdapter(
                this.messages,
                mutableMapOf(),
                intent.getStringExtra(Constants.EXTRA_IMAGE_ROOM) ?: "",
                {
                    messagePresenterImpl.loadMessages(
                        idProject,
                        idRoom,
                        token,
                        login,
                        this.messages.size / messagesAdapter.maxSizePage + 1
                    )
                },
                { pos, indexImage ->
                    startActivity(Intent(this@MessagesActivity, GalleryActivity::class.java).apply {
                        putExtra(
                            GalleryActivity.EXTRA_IMAGES,
                            messages[pos].getAttachments().toTypedArray()
                        )
                        putExtra(GalleryActivity.EXTRA_POS, indexImage)
                    })
                },
                { pos, indexFile ->
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(messages[pos].getAttachments()[indexFile])
                        })
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(
                            this,
                            R.string.messages_activity_not_fount,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
            messagesAdapter.maxSizePage = messages.size
            messagesAdapter.loadMore = true
            recyclerView.adapter = messagesAdapter
        } else {
            messagesAdapter.loadMore = messages.size == messagesAdapter.maxSizePage

            this.messages.addAll(messages)
            messagesAdapter.notifyDataSetChanged()
        }
    }

    override fun onSuccessLockUser(isBlocked: Boolean) {
        this.isBlocked = isBlocked
        setPopupMenu(
            isBlocked,
            isPaused
        )
    }

    override fun onSuccessPauseBot(isPaused: Boolean) {
        this.isPaused = isPaused
        setPopupMenu(
            isBlocked,
            isPaused
        )
    }

    override fun onSuccessSendMessage() {
        sendView.visibility = View.VISIBLE
        progressBarMessageView.visibility = View.GONE
        messageView.text.clear()
        llTemplate.isVisible = false
        messageId = null
    }

    override fun onNewMessage(message: Message) {
        messagesAdapter.messages.add(0, message)
        messagesAdapter.notifyItemInserted(0)
        recyclerView.scrollToPosition(0)
    }

    override fun onFailed(errorMess: String) {
        sendView.visibility = View.VISIBLE
        progressBarMessageView.visibility = View.GONE

        if (messagesAdapter.messages.size == 0) {
            progressBarView.visibility = View.GONE
            errorView.visibility = View.VISIBLE
            errorViewText.text = errorMess
        } else {
            Toast.makeText(this, errorMess, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        messagePresenterImpl.cancel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_TEMPLATE -> {
                if (resultCode == RESULT_OK) {
                    messageView.setText(
                        data?.getStringExtra(TemplateActivity.EXTRA_ANSWER_TEXT_RESULT).orEmpty()
                    )
                    messageId = data?.getIntExtra(
                        TemplateActivity.EXTRA_ANSWER_ID_RESULT,
                        Constants.DEFAULT_ID
                    )?.takeIf { it != Constants.DEFAULT_ID }.toString()
                    tvTemplate.text = getString(R.string.messages_template_id, messageId)
                    llTemplate.isVisible = true
                }
            }
        }
    }
}

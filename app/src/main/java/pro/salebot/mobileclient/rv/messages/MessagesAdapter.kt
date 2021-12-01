package pro.salebot.mobileclient.rv.messages

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.item_message.view.*
import pro.salebot.mobileclient.R
import pro.salebot.mobileclient.models.Message
import pro.salebot.mobileclient.rv.rooms.LoadMoreViewHolder
import pro.salebot.mobileclient.utils.PlayerController
import pro.salebot.mobileclient.utils.views.FileView
import pro.salebot.mobileclient.utils.views.VoiceView
import java.text.SimpleDateFormat
import java.util.*


class MessagesAdapter(
    val messages: MutableList<Message>,
    val voiceViews: MutableMap<Pair<Int, Int>, VoiceView> = mutableMapOf(),
    val imageUrl: String = "",
    val callbackLoadmore: () -> Unit,
    val callbackImageClick: (index: Int, indexImage: Int) -> Unit,
    val callbackFileClick: (index: Int, indexFile: Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var loadMore = false
    var maxSizePage = 0

    override fun getItemViewType(position: Int) =
        if (position == messages.size)
            MessageType.LOAD_MORE.ordinal
        else if (messages[position].client_replica != null && messages[position].client_replica!!)
            MessageType.MESSAGE.ordinal
        else
            MessageType.MY_MESSAGE.ordinal


    override fun onCreateViewHolder(parent: ViewGroup, type: Int) =
        when (type) {
            MessageType.MY_MESSAGE.ordinal -> MyMessageViewHolder.newInstance(parent)
            MessageType.MESSAGE.ordinal -> MessageViewHolder.newInstance(parent)
            else -> LoadMoreViewHolder.newInstance(parent)
        }

    override fun getItemCount() = if (loadMore) messages.size + 1 else messages.size

    override fun onBindViewHolder(h: RecyclerView.ViewHolder, p: Int) {
        when (h) {
            is MessageViewHolder -> {
                h.itemView.textView.text = messages[p].text?.trim() ?: ""
                h.itemView.dateView.text =
                    SimpleDateFormat(
                        "dd.MM.yyyy HH:mm",
                        Locale.getDefault()
                    ).format(messages[p].created_at)
                Glide.with(h.itemView.imageView)
                    .load(imageUrl)
                    .error(R.drawable.ic_no_avatar)
                    .into(h.itemView.imageView)

                loadAttachments(
                    h.itemView.context,
                    p,
                    h.itemView.attachmentsView
                )
                if (!messages[p].delivered) {
                    h.itemView.ivError.visibility = View.VISIBLE
                    h.itemView.tvError.visibility = View.VISIBLE
                } else {
                    h.itemView.ivError.visibility = View.GONE
                    h.itemView.tvError.visibility = View.GONE
                }
            }
            is MyMessageViewHolder -> {
                h.itemView.textView.text = messages[p].text?.trim() ?: ""
                h.itemView.dateView.text =
                    SimpleDateFormat(
                        "dd.MM.yyyy HH:mm",
                        Locale.getDefault()
                    ).format(messages[p].created_at)

                loadAttachments(
                    h.itemView.context,
                    p,
                    h.itemView.attachmentsView
                )

                if (!messages[p].delivered) {
                    h.itemView.ivError.visibility = View.VISIBLE
                    h.itemView.tvError.visibility = View.VISIBLE
                } else {
                    h.itemView.ivError.visibility = View.GONE
                    h.itemView.tvError.visibility = View.GONE
                }
            }
            else -> {
                callbackLoadmore()
            }
        }
    }

    private val imageFormats = listOf("jpg", "jpeg", "jpe", "png", "bmp")
    private val voiceFormats = listOf("ogg", "mp3", "wav")

    private fun loadAttachments(
        ctx: Context,
        p: Int,
        linearLayout: LinearLayout
    ) {
        try {
            linearLayout.removeAllViews()
            if (!messages[p].getAttachments().isNullOrEmpty())
                messages[p].getAttachments().forEachIndexed { index, attach ->
                    val link = messages[p].getAttachments()[index]
                    var view = linearLayout.findViewById<View>(index)

                    if (view == null) {
                        if (voiceFormats.contains(link.substringAfterLast("."))) {
                            if (voiceViews.containsKey(p to index)) {
                                view = voiceViews[p to index]
                            } else {
                                view = createVoiceView(ctx, index, p)
                                voiceViews[p to index] = view
                            }
                        } else if (imageFormats.contains(link.substringAfterLast("."))) {
                            view = createImageView(ctx, index, p)
                            Glide.with(ctx)
                                .load(link)
                                .into(view as ImageView)
                        } else {
                            view = createFileView(ctx, index, p)
                        }
                    }
                    linearLayout.addView(view)

                }
        } catch (e: IllegalStateException) {

        }
    }

    private fun createFileView(
        ctx: Context,
        index: Int,
        pos: Int
    ) = FileView(ctx).apply {
        setOnClickListener {
            callbackFileClick(pos, index)
        }
        val link = messages[pos].getAttachments()[index]
        setFormatName(link.substringAfterLast("."))
        setFileName(link.substringAfterLast("/"))
    }

    private fun createVoiceView(
        ctx: Context,
        index: Int,
        pos: Int
    ) = VoiceView(ctx).apply {
        val link = messages[pos].getAttachments()[index]
        val playerController = PlayerController(ctx)
        playerController.create(this, link)
        setPlayerController(playerController)
    }

    private fun createImageView(
        ctx: Context,
        index: Int,
        pos: Int
    ) = RoundedImageView(ctx).apply {
        setOnClickListener {
            callbackImageClick(pos, index)
        }
        id = index
        scaleType = ImageView.ScaleType.CENTER_CROP
        setCornerRadius(20f, 20f, 20f, 20f)
        val pixels = toDp(ctx, 100)
        layoutParams =
            LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pixels).apply {
                setMargins(
                    0,
                    5,
                    0,
                    5
                )
            }
    }

    private fun toDp(context: Context, dp: Int): Int {
        val scale = context.resources.displayMetrics.density
        return (100 * scale + 0.5f).toInt()
    }

    fun add(message: Message) {
        val lastIndex = messages.indexOfLast { it.id.isNullOrEmpty() }
        if (lastIndex != RecyclerView.NO_POSITION) {
            messages[lastIndex] = message
            notifyDataSetChanged()
        } else {
            messages.add(0, message)
            notifyItemInserted(0)
        }
    }

    fun addSendMessage(message: Message) {
        messages.add(0, message)
        notifyItemInserted(0)
    }

    fun update(message: Message) {
        if (messages[0].delivered != message.delivered) {
            val indexLast = messages.indexOfLast { it.id.isNullOrEmpty() }
            messages[indexLast] = message
            notifyItemChanged(indexLast)
        }
    }

}
package pro.salebot.mobileclient.rv.rooms

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_room.view.*
import pro.salebot.mobileclient.R
import pro.salebot.mobileclient.models.Room
import java.text.SimpleDateFormat
import java.util.*


class RoomsAdapter(
    var roomsList: MutableList<Room>,
    private val roomsAdapterListener: RoomsAdapterListener?
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int) = if (position == roomsList.size)
        RoomsType.LOAD_MORE.ordinal else RoomsType.ITEM.ordinal


    override fun onCreateViewHolder(parent: ViewGroup, type: Int) = when (type) {
            RoomsType.ITEM.ordinal -> RoomsViewHolder.newInstance(parent)
            else -> LoadMoreViewHolder.newInstance(parent)
        }

    override fun getItemCount() = if (loadmore) roomsList.size+1 else roomsList.size

    override fun onBindViewHolder(h: RecyclerView.ViewHolder, p: Int) {

        when (h) {
            is RoomsViewHolder -> {
                h.itemView.setOnClickListener { roomsAdapterListener?.onItemClick(roomsList[p]) }
                h.itemView.setOnLongClickListener { roomsAdapterListener?.onItemLongClick(p); true }
                if (roomsList[p].last_message != null) {
                    h.itemView.dateCreateView.text = SimpleDateFormat(
                        "dd MMMM yyyy HH:mm",
                        Locale.getDefault()
                    ).format(roomsList[p].updated_at)

                    h.itemView.nameView.text = roomsList[p].name

                    if (roomsList[p].last_message?.text.isNullOrEmpty()) {
                        h.itemView.lastMessageView.text = h.itemView.context.getString(R.string.attach_messages)
                    } else {
                        h.itemView.lastMessageView.text = roomsList[p].last_message?.text
                    }
                } else {
                    h.itemView.myMessageView.visibility = View.GONE
                    h.itemView.dateCreateView.text = SimpleDateFormat(
                        "dd MMMM yyyy",
                        Locale.getDefault()
                    ).format(roomsList[p].created_at)

                    h.itemView.nameView.text = roomsList[p].name

                    h.itemView.lastMessageView.text = h.itemView.context.getString(R.string.no_messages)
                }
//                Glide.with(h.itemView.imageView)
//                    .load(roomsList[p].avatar)
//                    .error(R.drawable.ic_no_avatar)
//                    .into(h.itemView.imageView)

                Glide.with(h.itemView.imageView).clear(h.itemView.imageView)
//                if (roomsList[p].image != null) {
//                    h.itemView.imageView.setImageBitmap(roomsList[p].image)
//                } else
                    Glide
                        .with(h.itemView.imageView)
//                        .asBitmap()
                        .load(roomsList[p].avatar)
                        .error(R.drawable.ic_no_avatar)
                        .dontAnimate()
                        .into(h.itemView.imageView)
//                        .into(object : SimpleTarget<Bitmap>() {
//                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
//                                // TODO Auto-generated method stub
//                                h.itemView.imageView.setImageBitmap(resource)
//                                roomsList[p].image = resource
//                            }
//
//                        })

                h.itemView.myMessageView.visibility =
                    if (roomsList[p].last_message != null && !roomsList[p].last_message!!.client_replica!!) {
                        h.itemView.llbgText.setBackgroundResource(R.drawable.bg_select)
                        View.VISIBLE
                    } else {
                        h.itemView.llbgText.background = null
                        View.GONE
                    }

            }
            is LoadMoreViewHolder -> {
                roomsAdapterListener?.onLoadmore()
            }
        }

    }

    var loadmore = false

    fun clearAll() {
        roomsList.clear()
        notifyDataSetChanged()
    }

    fun update(rl: MutableList<Room>) {
        roomsList.clear()
        roomsList.addAll(rl)
        notifyDataSetChanged()
    }

    fun delete(index: Int) {
        roomsList.removeAt(index)
        notifyItemRemoved(index)
    }

    fun loadMore(lm: Boolean) {
        loadmore = lm
        notifyDataSetChanged()
    }

}
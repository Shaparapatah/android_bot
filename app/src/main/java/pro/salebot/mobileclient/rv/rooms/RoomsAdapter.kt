package pro.salebot.mobileclient.rv.rooms

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_room.view.*
import pro.salebot.mobileclient.R
import pro.salebot.mobileclient.models.Project
import pro.salebot.mobileclient.models.Room
import java.text.SimpleDateFormat
import java.util.*


class RoomsAdapter(
    var roomsList: MutableList<Room>,
    private val roomsAdapterListener: RoomsAdapterListener?,
    private val showMenuDelete: (Boolean) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /** Добавил поле 22.03.2022 */
    private var isEnable = false
    private val itemSelectedList = mutableListOf<Int>()


    override fun getItemViewType(position: Int) = if (position == roomsList.size)
        RoomsType.LOAD_MORE.ordinal else RoomsType.ITEM.ordinal


    override fun onBindViewHolder(h: RecyclerView.ViewHolder, position: Int) {
        when (h) {
            is RoomsViewHolder -> {
                val item = roomsList[position]
                h.iv.visibility = View.GONE
                /** Добавил поле 22.03.2022*/
                h.itemView.setOnLongClickListener {
                    selectedItem(h, item, position)
                    true
                }
                h.itemView.setOnClickListener {
                    if (itemSelectedList.contains(position)) {
                        itemSelectedList.remove(position)
                        h.iv.visibility = View.GONE
                        item.selected = false
                        if (itemSelectedList.isEmpty()) {
                            showMenuDelete(false)
                            isEnable = false
                        }
                    } else if (isEnable) {
                        selectedItem(h, item, position)
                    }
                }

//                    h.itemView.setOnClickListener {
//                        roomsAdapterListener?.onItemClick(roomsList[position])
//                        isEnable = true
//                    }


                if (roomsList[position].last_message != null) {
                    h.itemView.dateCreateView.text = SimpleDateFormat(
                        "dd MMMM yyyy HH:mm",
                        Locale.getDefault()
                    ).format(roomsList[position].updated_at)

                    h.itemView.nameView.text = roomsList[position].name

                    if (roomsList[position].last_message?.text.isNullOrEmpty()) {
                        h.itemView.lastMessageView.text =
                            h.itemView.context.getString(R.string.attach_messages)
                    } else {
                        h.itemView.lastMessageView.text = roomsList[position].last_message?.text
                    }
                } else {
                    h.itemView.myMessageView.visibility = View.GONE
                    h.itemView.dateCreateView.text = SimpleDateFormat(
                        "dd MMMM yyyy",
                        Locale.getDefault()
                    ).format(roomsList[position].created_at)

                    h.itemView.nameView.text = roomsList[position].name

                    h.itemView.lastMessageView.text =
                        h.itemView.context.getString(R.string.no_messages)
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
                    .load(roomsList[position].avatar)
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
                    if (roomsList[position].last_message != null && !roomsList[position]
                            .last_message!!
                            .client_replica!!
                    ) {
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

        /** Добавил поле 22.03.2022 */
    }

    override fun onCreateViewHolder(parent: ViewGroup, type: Int) =
        when (type) {
            RoomsType.ITEM.ordinal -> RoomsViewHolder.newInstance(parent)
            else -> LoadMoreViewHolder.newInstance(parent)
        }


    override fun getItemCount() = if (loadmore) roomsList.size + 1 else roomsList.size

    var loadmore = false


    /** Добавил поле 22.03.2022
     * изменил 24.03.2022 */
    private fun selectedItem(h: RoomsViewHolder, item: Room, position: Int) {
        isEnable = true
            itemSelectedList.add(position)
            item.selected = true
            h.iv.visibility = View.VISIBLE
            showMenuDelete(true)
    }

    /** Доавибл поле 22.03.2022 */
    fun deleteSelectedItem() {
        if (itemSelectedList.isNotEmpty()) {
            roomsList.removeAll { item -> item.selected }
            isEnable = false
            itemSelectedList.clear()
        }
        notifyDataSetChanged()
    }

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
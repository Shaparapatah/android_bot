package pro.salebot.mobileclient.rv.messages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pro.salebot.mobileclient.R

class MessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    companion object {
        fun newInstance(parent: ViewGroup) = MessageViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        )
    }
}
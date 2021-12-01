package pro.salebot.mobileclient.rv.messages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pro.salebot.mobileclient.R

class MyMessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    companion object {
        fun newInstance(parent: ViewGroup) = MyMessageViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_my_message, parent, false)
        )
    }
}
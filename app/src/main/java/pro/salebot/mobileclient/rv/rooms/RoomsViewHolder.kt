package pro.salebot.mobileclient.rv.rooms

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pro.salebot.mobileclient.R

class RoomsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val iv: ImageView = itemView.findViewById(R.id.checkView)
    val tv: TextView = itemView.findViewById(R.id.nameView)


    companion object {
        fun newInstance(parent: ViewGroup) = RoomsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_room, parent, false)
        )
    }

}
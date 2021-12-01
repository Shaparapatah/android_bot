package pro.salebot.mobileclient.rv.rooms

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pro.salebot.mobileclient.R

class LoadMoreViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    companion object {
        fun newInstance(parent:ViewGroup) = LoadMoreViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_loadmore, parent, false)
        )
    }

}
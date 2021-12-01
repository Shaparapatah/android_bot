package pro.salebot.mobileclient.mvp.profile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pro.salebot.mobileclient.R

class LoadMoreAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        LoadMoreViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_loadmore, parent, false)
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    }

    override fun getItemCount() = 1

}

class LoadMoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

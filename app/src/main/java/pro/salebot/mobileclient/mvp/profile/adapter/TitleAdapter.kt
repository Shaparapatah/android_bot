package pro.salebot.mobileclient.mvp.profile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_title.view.*
import pro.salebot.mobileclient.R

class TitleAdapter(
    val title: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TitleViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_title, parent, false)
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        with(holder.itemView) {
            tvTitle.text = title
        }
    }

    override fun getItemCount() = 1

}

class TitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

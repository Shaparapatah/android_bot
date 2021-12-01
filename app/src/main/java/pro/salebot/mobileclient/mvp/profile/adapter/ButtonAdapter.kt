package pro.salebot.mobileclient.mvp.profile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_button.view.*
import pro.salebot.mobileclient.R

class ButtonAdapter(
    val text: String,
    val cb: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ButtonViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_button, parent, false)
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        with(holder.itemView) {
            btnAction.text = text
            btnAction.setOnClickListener {
                cb.invoke()
            }
        }
    }

    override fun getItemCount() = 1

}

class ButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

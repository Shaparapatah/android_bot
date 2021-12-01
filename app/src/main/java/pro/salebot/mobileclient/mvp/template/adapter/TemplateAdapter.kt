package pro.salebot.mobileclient.mvp.template.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_template.view.*
import pro.salebot.mobileclient.R
import pro.salebot.mobileclient.mvp.template.entity.TemplateItem

class TemplateAdapter(
    var list: List<TemplateItem> = emptyList(),
    val cb: (TemplateItem) -> Unit
) : RecyclerView.Adapter<TemplateViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateViewHolder =
        TemplateViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_template, parent, false)
        )

    override fun onBindViewHolder(holder: TemplateViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            cb.invoke(list[position])
        }
        holder.itemView.tvAnswer.text = String.format(
            "#%s %s",
            list[position].id,
            list[position].answer
        )
        holder.itemView.ivBackground.setBackgroundResource(list[position].messageType.backgroundRes)
    }

    override fun getItemCount() = list.size
}

class TemplateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
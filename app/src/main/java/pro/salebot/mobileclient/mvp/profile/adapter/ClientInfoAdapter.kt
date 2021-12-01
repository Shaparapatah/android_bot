package pro.salebot.mobileclient.rv.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_client_info.view.*
import pro.salebot.mobileclient.R
import pro.salebot.mobileclient.mvp.profile.data.VariablesMo

class ClientInfoAdapter(
    var variablesList: List<VariablesMo> = emptyList(),
    private val clickListener: ((String, String) -> Unit)? = null
) : RecyclerView.Adapter<ClientInfoVH>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int) = ClientInfoVH(
        LayoutInflater
            .from(p0.context)
            .inflate(R.layout.item_client_info, p0, false)
    )

    override fun getItemCount() = variablesList.size

    override fun onBindViewHolder(h: ClientInfoVH, pos: Int) {
        with(h.itemView) {
            clickListener?.let { listener ->
                setOnClickListener {
                    listener.invoke(
                        variablesList[pos].title,
                        variablesList[pos].text
                    )
                }
            }
            tvTitle.text = variablesList[pos].title
            tvText.text = variablesList[pos].text
        }
    }

}

class ClientInfoVH(itemView: View) : RecyclerView.ViewHolder(itemView)
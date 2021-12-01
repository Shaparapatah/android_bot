package pro.salebot.mobileclient.mvp.profile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_profile_info.view.*
import pro.salebot.mobileclient.R
import pro.salebot.mobileclient.mvp.profile.data.ProfileInfoData

class ProfileInfoAdapter(
    var data: ProfileInfoData? = null,
    var cb: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ProfileInfoViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_profile_info, parent, false)
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        with(holder.itemView) {
            Glide.with(this)
                .load(data?.imageUrl)
                .error(R.drawable.ic_no_avatar)
                .into(ivAvatar)

            tvName.text = data?.name

            tvTimeAction.text =
                context.getString(R.string.profile_time_action, data?.lastUpdate.orEmpty())
            tvClientId.text =
                context.getString(R.string.profile_client_id, data?.clientId.orEmpty())
            if (data?.responsibleId != null) {
                tvResponsibleId.text = context.getString(
                    R.string.profile_responsible_id,
                    data?.responsibleId.toString()
                )
            } else {
                tvResponsibleId.text = context.getString(R.string.profile_responsible_id_empty)
            }
            if (data?.isMyClient == true) {
                btnClientToMe.setText(R.string.refuse_client)
            } else {
                btnClientToMe.setText(R.string.take_me_client)
            }

            btnClientToMe.setOnClickListener {
                cb.invoke()
            }

        }
    }

    override fun getItemCount() = if (data != null) 1 else 0

}

class ProfileInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

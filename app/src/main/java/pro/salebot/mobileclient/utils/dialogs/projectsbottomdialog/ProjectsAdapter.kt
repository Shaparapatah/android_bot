package pro.salebot.mobileclient.utils.dialogs.projectsbottomdialog

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_menu.view.*
import pro.salebot.mobileclient.R
import pro.salebot.mobileclient.database.DataBaseParams
import pro.salebot.mobileclient.models.Project

class ProjectsAdapter(
    val projectList: List<Project>,
    val onItemClick: (project: Project, pos: Int) -> Unit
) : RecyclerView.Adapter<ProjectsViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int) = ProjectsViewHolder(
        LayoutInflater.from(p0.context).inflate(R.layout.item_menu, p0, false)
    )

    override fun getItemCount() = projectList.size

    override fun onBindViewHolder(p0: ProjectsViewHolder, p1: Int) {
        with(p0.itemView) {
            textMenuView.text = projectList[p1].name
            if (DataBaseParams(context).getKey(DataBaseParams.KEY_ID_NOTIFICATION + projectList[p1].id)
                    .isNullOrEmpty()
            ) {
                imageMenuView.visibility = View.GONE
            } else {
                imageMenuView.visibility = View.VISIBLE
            }
            if (projectList[p1].check) {
                this.setBackgroundResource(R.drawable.bg_select)
            } else {
                val typedValue = TypedValue()
                context!!.theme.resolveAttribute(
                    android.R.attr.selectableItemBackground,
                    typedValue,
                    true
                )
                if (typedValue.resourceId != 0) {
                    this.setBackgroundResource(typedValue.resourceId)
                } else {
                    this.setBackgroundColor(typedValue.data)
                }
            }
            this.setOnClickListener {
                onItemClick(projectList[p1], p1)
            }
        }
    }
}

class ProjectsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

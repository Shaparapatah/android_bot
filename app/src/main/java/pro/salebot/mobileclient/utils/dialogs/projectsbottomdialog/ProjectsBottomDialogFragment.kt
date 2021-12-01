package pro.salebot.mobileclient.utils.dialogs.projectsbottomdialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.layout_projects_bottom_sheet.*
import pro.salebot.mobileclient.R
import pro.salebot.mobileclient.database.DataBaseParams
import pro.salebot.mobileclient.models.Project

class ProjectsBottomDialogFragment : BottomSheetDialogFragment() {

    private var listener: ProjectsBottomDialogListener? = null

    companion object {

        private const val ARG_LOGIN = "ARG_LOGIN"
        private const val ARG_PROJECTS = "ARG_PROJECTS"

        fun newInstance(
            login: String,
            projects: List<Project>,
            listener: ProjectsBottomDialogListener
        ) =
            ProjectsBottomDialogFragment()
                .apply {
                    arguments = Bundle().apply {
                        putString(ARG_LOGIN, login)
                        putSerializable(ARG_PROJECTS, projects.toTypedArray())
                    }
                    this.listener = listener
                }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_projects_bottom_sheet, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.get(ARG_PROJECTS)?.let {
            navList.adapter =
                ProjectsAdapter((it as? Array<Project>)?.toList() ?: emptyList()) { project, pos ->
                    listener?.onProjectClick(pos)
                    dismiss()
                }
        }
        arguments?.getString(ARG_LOGIN)?.let {
            emailView.text = it
        }
        val dataBaseParams = DataBaseParams(context)
//        sToggleNotif.isChecked = "0" != dataBaseParams.getKey(DataBaseParams.KEY_RUN_NOTIF)
//        sToggleNotif.setOnCheckedChangeListener { _, isChecked ->
//            context?.let {
//                if (isChecked) {
//                    dataBaseParams.setKey(DataBaseParams.KEY_RUN_NOTIF, 1)
//                } else {
//                    dataBaseParams.setKey(DataBaseParams.KEY_RUN_NOTIF, 0)
//                }
//                listener?.onNotifCheckedChangeListener(isChecked)
//            }
//        }

        logoutView.setOnClickListener {
            listener?.onLogout()
            dismiss()
        }
    }
}

interface ProjectsBottomDialogListener {
    fun onProjectClick(pos: Int)
    fun onNotifCheckedChangeListener(isChecked: Boolean)
    fun onLogout()
}
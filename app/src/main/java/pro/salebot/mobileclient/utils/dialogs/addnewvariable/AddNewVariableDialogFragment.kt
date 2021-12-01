package pro.salebot.mobileclient.utils.dialogs.addnewvariable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.layout_add_new_variable_bottom_sheet.*
import pro.salebot.mobileclient.R
import pro.salebot.mobileclient.utils.isVisible

class AddNewVariableDialogFragment : BottomSheetDialogFragment() {

    private var listener: ProjectsBottomDialogListener? = null

    private val isChangeVar
        get() = arguments?.getBoolean(ARG_CHANGE_VAR) ?: false
    private val variable
        get() = arguments?.getString(ARG_VARIABLE).orEmpty()
    private val value
        get() = arguments?.getString(ARG_VALUE).orEmpty()

    companion object {
        private const val ARG_CHANGE_VAR = "ARG_CHANGE_VAR"
        private const val ARG_VARIABLE = "ARG_VARIABLE"
        private const val ARG_VALUE = "ARG_VALUE"

        fun newInstance(
            isChangeVar: Boolean,
            variable: String?,
            value: String?
        ) = AddNewVariableDialogFragment()
            .apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_CHANGE_VAR, isChangeVar)
                    putString(ARG_VARIABLE, variable)
                    putString(ARG_VALUE, value)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_add_new_variable_bottom_sheet, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()

        btnAdd.setOnClickListener {
            listener?.onAddNewClick(
                tag.orEmpty(),
                etVariable.text.toString(),
                etValue.text.toString()
            )
            dismiss()
        }
    }

    private fun initView() {
        listener = requireContext() as ProjectsBottomDialogListener

        if (isChangeVar) {
            etVariable.setText(variable)
            etVariable.isEnabled = false
            etValue.setText(value)
            tvTitle.setText(R.string.change_variable)
            btnAdd.setText(R.string.change)
            btnDelete.isVisible = true
            spaceBtn.isVisible = true
            btnDelete.setOnClickListener {
                listener?.onDeleteClick(
                    tag.orEmpty(),
                    etVariable.text.toString()
                )
                dismiss()
            }
        }
    }

}

interface ProjectsBottomDialogListener {
    fun onAddNewClick(tag: String, variable: String, value: String)
    fun onDeleteClick(tag: String, variable: String)
}
package pro.salebot.mobileclient.utils.dialogs.writetowhatsapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.layout_add_new_variable_bottom_sheet.*
import pro.salebot.mobileclient.R

class AddNewVariableDialogFragment : BottomSheetDialogFragment() {

    private var listener: WriteToWhatsAppDialogListener? = null

    companion object {
        fun newInstance(listener: WriteToWhatsAppDialogListener) =
            AddNewVariableDialogFragment()
                .apply {
                    this.listener = listener
                }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_write_to_whatsapp_bottom_sheet, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnAdd.setOnClickListener {
            listener?.onAddNewClick(
                etVariable.text.toString(),
                etVariable.text.toString()
            )
            dismiss()
        }
    }
}

interface WriteToWhatsAppDialogListener {
    fun onAddNewClick(phone: String, name: String)
}
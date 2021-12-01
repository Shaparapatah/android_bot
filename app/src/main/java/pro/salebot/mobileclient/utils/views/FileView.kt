package pro.salebot.mobileclient.utils.views

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.layout_file.view.*
import pro.salebot.mobileclient.R

class FileView : ConstraintLayout {

    constructor(context: Context) :
            super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) :
            super(context, attrs) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        initView(context)
    }

    private fun initView(context: Context?) {
        inflate(context, R.layout.layout_file, this)
    }

    fun setFormatName(text: String) {
        tvFormat.text = text
    }

    fun setFileName(text: String) {
        tvName.text = text
    }

}
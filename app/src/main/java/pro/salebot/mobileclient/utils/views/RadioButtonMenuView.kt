package pro.salebot.mobileclient.utils.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRadioButton
import pro.salebot.mobileclient.R
import pro.salebot.mobileclient.utils.toPx


class RadioButtonMenuView : AppCompatRadioButton {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        val customAttrs = context?.obtainStyledAttributes(attrs, R.styleable.RadioButtonMenuView)

        try {
            val color =
                customAttrs?.getColor(R.styleable.RadioButtonMenuView_colorShape, Color.BLACK)
                    ?: Color.BLACK
            this@RadioButtonMenuView.color = color
        } finally {
            customAttrs?.recycle()
        }
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val customAttrs =
            context?.obtainStyledAttributes(attrs, R.styleable.RadioButtonMenuView, defStyleAttr, 0)

        try {
            val color =
                customAttrs?.getColor(R.styleable.RadioButtonMenuView_colorShape, Color.BLACK)
                    ?: Color.BLACK
            this@RadioButtonMenuView.color = color
        } finally {
            customAttrs?.recycle()
        }

    }

    private val circleWidth = 3f

    private var color: Int = Color.BLACK

//    private val paintCircle
//        get() = Paint().apply {
//            color = paintRect.color
//            style = if (isChecked) Paint.Style.FILL else Paint.Style.STROKE
//            strokeWidth = circleWidth
//        }

    private val paintText
        get() = Paint().apply {
            color = if (isChecked) textColors.defaultColor else paintRect.color
            style = Paint.Style.FILL
            textSize = this@RadioButtonMenuView.textSize
        }

    private val paintRect
        get() = Paint().apply {
            color = this@RadioButtonMenuView.color
            style = if (isChecked) Paint.Style.FILL else Paint.Style.STROKE
            strokeWidth = circleWidth
        }

    override fun onDraw(canvas: Canvas?) {
//        super.onDraw(canvas)

//        canvas?.drawCircle(
//            (width.toFloat() / 2),
//            (height.toFloat() / 2),
//            (if (width > height) height.toFloat() / 2 else width.toFloat() / 2) - (circleWidth / 2),
//            paintCircle
//        )

//        canvas?.drawRoundRect(
//            (width.toFloat() / 2) - paintText.measureText(text.toString()),
//            height.toFloat() / 2 - textSize,
//            (width.toFloat() / 2) + paintText.measureText(text.toString()),
//            height.toFloat() / 2 + textSize,
//            100f,
//            100f,
//            paintRect
//        )

        canvas?.drawRoundRect(
            5f.toPx(),
            height.toFloat() / 2 - textSize,
            width.toFloat() - 5f.toPx(),
            height.toFloat() / 2 + textSize,
            100f,
            100f,
            paintRect
        )

        canvas?.drawText(
            text.toString(),
            (width.toFloat() / 2) - (paintText.measureText(text.toString()) / 2),
            height.toFloat() / 2 + textSize / 2 - 1f.toPx(),
            paintText
        )

    }

//    override fun toggle() {
////        if (isChecked) {
//            if (parent is RadioGroup) {
//                (parent as RadioGroup).let {
//
//                    for (i in 0 until it.childCount) {
//                        (it.getChildAt(i) as RadioButtonMenuView).let{ rbmv ->
//                            rbmv.isChecked = rbmv.id == this.id
//                        }
//                    }
//                }
//            }
////        super.toggle()
////        } else {
////            isChecked = true
////        }
//    }

//    override fun setChecked(checked: Boolean) {
//        super.setChecked(checked)


////        val afm: AutofillManager? = context?.getSystemService(AutofillManager::class.java)
////        afm?.notifyValueChanged(this)
////        if (mChecked != checked) {
////            mChecked = checked
////    //        if (!mOnCheckedChangeListeners.isEmpty()) {
////    //            for (i in 0 until mOnCheckedChangeListeners.size()) {
////    //                mOnCheckedChangeListeners.get(i).onCheckedChanged(this, mChecked)
////    //            }
////    //        }
////            if (mChecked) {
////    //            refreshDrawableState()
////            } else {
////    //            setNormalState()
////            }
////        }
//    }

//    override fun isChecked(): Boolean {
//        return mChecked
//    }

//    override fun toggle() {
//
//
//
//        isChecked = !isChecked
//    }

    private var toggleListener: (() -> Unit)? = null

    override fun toggle() {
        super.toggle()
        toggleListener?.invoke()
    }

    fun setOnToggleListener(tl: () -> Unit) {
        toggleListener = tl
    }

    override fun setChecked(checked: Boolean) {
        super.setChecked(checked)
        invalidate()
        requestLayout()
    }

}

package pro.salebot.mobileclient.utils

import android.content.res.Resources
import android.util.Log
import android.view.View

fun log(msg: String) {
    Log.d("ENDLESS-SERVICE", msg)
}

fun Float.toDp(): Float = (this / Resources.getSystem().displayMetrics.density)

fun Float.toPx(): Float = (this * Resources.getSystem().displayMetrics.density)

var View.isVisible: Boolean
    get() = View.VISIBLE == visibility
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }

fun formatMenuCount(count: Int) =
    if (count >= 10000) {
        "10k+"
    } else {
        count.toString()
    }

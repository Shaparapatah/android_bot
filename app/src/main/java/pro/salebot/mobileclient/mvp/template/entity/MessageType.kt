package pro.salebot.mobileclient.mvp.template.entity

import androidx.annotation.DrawableRes
import com.google.gson.annotations.SerializedName
import pro.salebot.mobileclient.R

enum class MessageType(@DrawableRes val backgroundRes: Int) {
    @SerializedName("0")
    COLOR1(R.drawable.bg_template_color_1),

    @SerializedName("1")
    COLOR2(R.drawable.bg_template_color_2),

    @SerializedName("2")
    COLOR3(R.drawable.bg_template_color_3),

    @SerializedName("3")
    COLOR4(R.drawable.bg_template_color_4),

    @SerializedName("4")
    COLOR5(R.drawable.bg_template_color_5),

    @SerializedName("5")
    COLOR6(R.drawable.bg_template_color_6),

    @SerializedName("6")
    COLOR7(R.drawable.bg_template_color_7),

    @SerializedName("7")
    COLOR8(R.drawable.bg_template_color_8)
}
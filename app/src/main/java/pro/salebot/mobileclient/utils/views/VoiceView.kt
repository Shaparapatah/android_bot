package pro.salebot.mobileclient.utils.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.layout_voice.view.*
import pro.salebot.mobileclient.R
import pro.salebot.mobileclient.utils.PlayerController

class VoiceView : ConstraintLayout {

    private lateinit var playerController: PlayerController

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
        inflate(context, R.layout.layout_voice, this)
    }

    fun setTimeText(text: String) {
        tvTime.text = text
    }

    fun setProgress(progress: Int) {
        sbTime.progress = progress
    }

    fun setMax(value: Int) {
        sbTime.max = value
    }

    fun showPlayIcon(b: Boolean) {
        ivPlay.setImageResource(
            if (b)
                R.drawable.ic_play
            else
                R.drawable.ic_pause
        )
    }

    fun setOnSeekBarChangeListener(listener: SeekBar.OnSeekBarChangeListener) {
        sbTime.setOnSeekBarChangeListener(listener)
    }

    fun setOnPlayClickListener(listener: (View) -> Unit) {
        ivPlay.setOnClickListener(listener)
    }

    fun setPlayerController(playerController: PlayerController) {
        this.playerController = playerController
    }

    fun getPlayerController() = this.playerController

}

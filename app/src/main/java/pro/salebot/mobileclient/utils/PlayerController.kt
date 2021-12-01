package pro.salebot.mobileclient.utils

import android.content.Context
import android.media.MediaPlayer
import android.widget.SeekBar
import pro.salebot.mobileclient.utils.views.VoiceView
import java.text.SimpleDateFormat
import java.util.*

class PlayerController(val context: Context) {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var voiceView: VoiceView
    private var seekBarTouch = false
    private var isPrepared = false

    fun create(view: VoiceView, url: String) {
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            setOnCompletionListener {
                voiceView.setProgress(0)
                voiceView.showPlayIcon(true)
                voiceView.setTimeText("00:00")
            }
        }
        createVoiceView(view)
    }

    private fun createVoiceView(view: VoiceView) {
        voiceView = view
        voiceView.setProgress(0)
        voiceView.setTimeText("00:00")
        voiceView.setOnPlayClickListener {
            if (isPlaying()) {
                pause()
            } else {
                play()
            }
        }
        voiceView.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                voiceView.setTimeText(formatToTime(progress.toLong()))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                seekBarTouch = true
                pause()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBarTouch = false
                seekBar?.let {
                    mediaPlayer.seekTo(it.progress)
                }
                play()
            }
        })
    }

    private val updateTime: Runnable = object : Runnable {
        override fun run() {
            if (isPlaying()) {
                voiceView.setTimeText(formatToTime(mediaPlayer.currentPosition.toLong()))
                voiceView.setProgress(mediaPlayer.currentPosition)
                voiceView.postDelayed(this, 100)
            } else {
                voiceView.removeCallbacks(this)
            }
        }
    }

    private fun formatToTime(ms: Long) =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(Date(ms))

    fun pause() {
        if (isPlaying()) {
            mediaPlayer.pause()
        }
        voiceView.showPlayIcon(!isPlaying())
    }

    fun play() {
        if (!isPrepared) {
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                isPrepared = true
                voiceView.setMax(mediaPlayer.duration)
                play()
            }
            voiceView.showPlayIcon(false)
        } else {
            if (!isPlaying()) {
                mediaPlayer.start()
            }
            voiceView.showPlayIcon(!isPlaying())
            voiceView.postDelayed(updateTime, 100)
        }
    }

    fun isPlaying() = mediaPlayer.isPlaying

}
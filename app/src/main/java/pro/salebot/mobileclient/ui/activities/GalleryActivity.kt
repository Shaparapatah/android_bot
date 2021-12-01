package pro.salebot.mobileclient.ui.activities

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.transition.TransitionManager
import kotlinx.android.synthetic.main.activity_gallery.*
import pro.salebot.mobileclient.R
import pro.salebot.mobileclient.utils.ImagesViewPagerAdapter
import java.util.*

class GalleryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        setContentView(R.layout.activity_gallery)
        init()
    }

    private fun init() {
        val imageList = ArrayList<String>()
        Collections.addAll(imageList, *intent.getStringArrayExtra(EXTRA_IMAGES))

        backView.setOnClickListener { finish() }

        val adapter = ImagesViewPagerAdapter(supportFragmentManager, imageList) {
            startTrans()
            if (backView.visibility == View.VISIBLE) {
                backView.visibility = View.GONE
                tabLayout.alpha = 0.5f
            } else {
                backView.visibility = View.VISIBLE
                tabLayout.alpha = 1f
            }
        }
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
        viewPager.currentItem = intent.getIntExtra(EXTRA_POS, 0)
    }

    private fun startTrans() {
        val transContainer = window.decorView as ViewGroup
        TransitionManager.beginDelayedTransition(transContainer)
    }

    companion object {
        var EXTRA_IMAGES = "EXTRA_IMAGES"
        var EXTRA_POS = "EXTRA_POS"
    }

}

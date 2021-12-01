package pro.salebot.mobileclient.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import pro.salebot.mobileclient.ui.fragments.ImageFragment

class ImagesViewPagerAdapter(
    fm: FragmentManager,
    var imageList: List<String>,
    var listener: () -> Unit
) : FragmentStatePagerAdapter(fm) {

    override fun getItem(i: Int): Fragment {
        val fragment = ImageFragment.newInstance(imageList[i])
        fragment.setListener(listener)
        return fragment
    }

    override fun getCount(): Int {
        return imageList.size
    }
}

package pro.salebot.mobileclient.ui.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_image_gallery.*
import pro.salebot.mobileclient.R

class ImageFragment : Fragment() {

    private var image: String? = null

    private var listener: (() -> Unit?)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            image = arguments!!.getString(ARG_IMAGE)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.fragment_image_gallery, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageView!!.setOnClickListener {
            listener?.let { it() }
        }

        if (context != null) {
            Glide.with(context!!)
                .load(image)
                .into(imageView!!)
        }

    }

    fun setListener(listener: () -> Unit) {
        this.listener = listener
    }

    companion object {
        var ARG_IMAGE = "ARG_IMAGE"
        fun newInstance(image: String): ImageFragment {
            val fragment = ImageFragment()
            val args = Bundle()
            args.putString(ARG_IMAGE, image)
            fragment.arguments = args
            return fragment
        }
    }
}

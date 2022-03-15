package pro.salebot.mobileclient.rv.rooms.menu

import android.annotation.SuppressLint
import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.item_menu.view.*
import pro.salebot.mobileclient.R
import pro.salebot.mobileclient.database.DataBaseParams
import pro.salebot.mobileclient.models.Project

class MenuAdapter(val context: Context?, val menuItems: List<Project>) : BaseAdapter() {
    @SuppressLint("ViewHolder")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val v = View.inflate(context, R.layout.item_menu, null)

//        v.itemMenuView.setBackgroundColor(context!!.resources.getColor(R.color.colorPrimary))
        v.textMenuView.text = menuItems[p0].name

        if (menuItems[p0].check)
//            v.itemMenuView.setBackgroundColor(context!!.resources.getColor(R.color.select))
            v.itemMenuView.setBackgroundResource(R.drawable.bg_select)
        else {

            val typedValue = TypedValue()

            // I used getActivity() as if you were calling from a fragment.
            // You just want to call getTheme() on the current activity, however you can get it
            context!!.theme.resolveAttribute(
                android.R.attr.selectableItemBackground,
                typedValue,
                true
            )

            // it's probably a good idea to check if the color wasn't specified as a resource
            if (typedValue.resourceId != 0) {
                v.itemMenuView.setBackgroundResource(typedValue.resourceId)
            } else {
                // this should work whether there was a resource id or not
                v.itemMenuView.setBackgroundColor(typedValue.data)
            }
        }

        if (DataBaseParams(context).getKey(DataBaseParams.KEY_ID_NOTIFICATION + menuItems[p0].id)
                .isNullOrEmpty()
        ) {
            v.imageMenuView.visibility = View.GONE
        } else {
            v.imageMenuView.visibility = View.VISIBLE
        }

        return v
    }

    fun updateCheck(pos: Int) {
        for (i in 0 until menuItems.size) {
            menuItems[i].check = pos == i
        }
        notifyDataSetChanged()
    }

    override fun getItem(p0: Int) = menuItems[p0]

    override fun getItemId(p0: Int) = p0.toLong()

    override fun getCount() = menuItems.size

}
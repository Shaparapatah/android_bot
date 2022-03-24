package pro.salebot.mobileclient.rv.rooms

import pro.salebot.mobileclient.models.Room

interface RoomsAdapterListener {

    fun onItemClick(room: Room)

    fun onItemLongClick(index: Int)

    fun onLoadmore()

}
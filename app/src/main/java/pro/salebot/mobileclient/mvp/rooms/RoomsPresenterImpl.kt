package pro.salebot.mobileclient.mvp.rooms

import pro.salebot.mobileclient.models.Project
import pro.salebot.mobileclient.models.RoomsData

class RoomsPresenterImpl(
    var roomsView: RoomsView?
) : RoomsPresenter, RoomsPresenterListener {

    private val roomsIterator = RoomsIteratorImpl(this)

    override fun loadRooms(
        token: String,
        email: String,
        idProject: String,
        requestType: String,
        searchText: String
    ) {
        roomsIterator.startLoadingRooms(token, email, idProject, requestType, searchText)
    }

    override fun loadMore(
        token: String,
        email: String,
        idProject: String,
        requestType: String,
        searchText: String,
        page: Int
    ) {
        roomsIterator.startLoadMoreRooms(token, email, idProject, requestType, searchText, page)
    }

    override fun loadChannels(token: String, email: String) {
        roomsIterator.startLoadMenuItems(token, email)
    }

    override fun subscribeNotification(
        isSubscribe: Boolean,
        token: String,
        email: String,
        idProject: String
    ) {
        roomsIterator.sendSubscribeNotification(isSubscribe, token, email, idProject)
    }

    override fun deleteRoom(
        index: Int,
        token: String,
        email: String,
        idRoom: String,
        idProject: String
    ) {
        roomsIterator.startDeleteRoom(index, token, email, idRoom, idProject)
    }

    override fun loadUpdate(
        token: String,
        email: String,
        idProject: String,
        requestType: String,
        searchText: String,
        pages: Int
    ) {
        roomsIterator.startUpdate(token, email, idProject, requestType, searchText, pages)
    }

    override fun sendPushId(token: String, login: String, pushId: String) {
        roomsIterator.startSendPushId(token, login, pushId)
    }

    override fun onSuccess(roomsList: RoomsData) {
        roomsView?.onSuccess(roomsList)
    }

    override fun onFailed(errorMess: String) {
        roomsView?.onFailed(errorMess)
    }

    override fun onSuccessMenu(menuItems: List<Project>) {
        roomsView?.onSuccessMenu(menuItems)
    }

    override fun onSuccessDeleteRoom(index: Int) {
        roomsView?.onSuccessDeleteRoom(index)
    }

    override fun onFailedDeleteRoom(errorMess: String) {
        roomsView?.onFailedDeleteRoom(errorMess)
    }

    override fun onUpdateRoom(roomsList: RoomsData) {
        roomsView?.onUpdateRoom(roomsList)
    }

    fun cancel() {
        roomsView = null
    }

}
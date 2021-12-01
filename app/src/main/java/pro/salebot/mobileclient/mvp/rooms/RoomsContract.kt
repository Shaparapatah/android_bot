package pro.salebot.mobileclient.mvp.rooms

import pro.salebot.mobileclient.models.Project
import pro.salebot.mobileclient.models.RoomsData
import pro.salebot.mobileclient.mvp.BasePresenterListener

interface RoomsView {
    fun onSuccess(roomsList: RoomsData)
    fun onFailed(errorMess: String)
    fun onSuccessMenu(menuItems: List<Project>)
    fun onSuccessDeleteRoom(index: Int)
    fun onFailedDeleteRoom(errorMess: String)
    fun onUpdateRoom(roomsList: RoomsData)
}

interface RoomsPresenter {
    fun loadRooms(
        token: String,
        email: String,
        idProject: String,
        requestType: String,
        searchText: String
    )

    fun loadMore(
        token: String,
        email: String,
        idProject: String,
        requestType: String,
        searchText: String,
        page: Int
    )

    fun loadChannels(token: String, email: String)
    fun deleteRoom(
        index: Int,
        token: String,
        email: String,
        idRoom: String,
        idProject: String
    )

    fun loadUpdate(
        token: String,
        email: String,
        idProject: String,
        requestType: String,
        searchText: String,
        pages: Int
    )

    fun sendPushId(token: String, login: String, pushId: String)

    fun subscribeNotification(
        isSubscribe: Boolean,
        token: String,
        email: String,
        idProject: String
    )
}

interface RoomsPresenterListener : BasePresenterListener {
    fun onSuccess(roomsList: RoomsData)
    fun onSuccessMenu(menuItems: List<Project>)
    fun onSuccessDeleteRoom(index: Int)
    fun onFailedDeleteRoom(errorMess: String)
    fun onUpdateRoom(roomsList: RoomsData)
}

interface RoomsIterator {
    fun startLoadingRooms(
        token: String,
        email: String,
        idProject: String,
        requestType: String,
        searchText: String
    )

    fun startLoadMoreRooms(
        token: String,
        email: String,
        idProject: String,
        requestType: String,
        searchText: String,
        page: Int
    )

    fun startLoadMenuItems(
        token: String,
        email: String
    )

    fun startDeleteRoom(
        index: Int,
        token: String,
        email: String,
        idRoom: String,
        idProject: String
    )

    fun startUpdate(
        token: String,
        email: String,
        idProject: String,
        requestType: String,
        searchText: String,
        pages: Int
    )

    fun startSendPushId(token: String, login: String, pushId: String)

    fun sendSubscribeNotification(
        isSubscribe: Boolean,
        token: String,
        email: String,
        idProject: String
    )
}
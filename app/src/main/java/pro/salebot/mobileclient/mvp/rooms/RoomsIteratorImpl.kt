package pro.salebot.mobileclient.mvp.rooms

import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import pro.salebot.mobileclient.Constants
import pro.salebot.mobileclient.api.RequestServer
import pro.salebot.mobileclient.api.models.Request
import pro.salebot.mobileclient.models.Project
import pro.salebot.mobileclient.models.Room
import pro.salebot.mobileclient.models.RoomsData
import pro.salebot.mobileclient.utils.processingFailure
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RoomsIteratorImpl(
    val presenterListener: RoomsPresenterListener?
) : RoomsIterator {

    private val requestServer: RequestServer by lazy {
        RequestServer.create()
    }

    override fun startLoadingRooms(
        token: String,
        email: String,
        idProject: String,
        requestType: String,
        searchText: String
    ) {
        val call = requestServer.getRooms(idProject, token, email, requestType, searchText)

        call.enqueue(object : Callback<RoomsData> {
            override fun onResponse(call: Call<RoomsData>?, response: Response<RoomsData>?) {
                response?.let {
                    if (response.isSuccessful && it.body() != null) {
                        presenterListener?.onSuccess(it.body()!!)
                    } else {
                        try {
                            val jObjError = JSONObject(it.errorBody()!!.string())
                            presenterListener?.onFailed(jObjError.getString("error"))
                        } catch (e: JSONException) {
                            if (it.raw().message().isNullOrEmpty()) {
                                presenterListener?.onFailed("unknown error")
                            } else {
                                presenterListener?.onFailed(it.raw().message())
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<RoomsData>?, t: Throwable?) {
                t.processingFailure(presenterListener)
            }
        })
    }

    override fun startLoadMoreRooms(
        token: String,
        email: String,
        idProject: String,
        requestType: String,
        searchText: String,
        page: Int
    ) {
//        presenterListener?.onFailed("Error load more")

        val call = requestServer
            .getRooms(idProject, token, email, page.toString(), requestType, searchText)

        call.enqueue(object : Callback<RoomsData> {
            override fun onResponse(call: Call<RoomsData>?, response: Response<RoomsData>?) {
                response?.let {
                    if (response.isSuccessful && it.body() != null) {
                        presenterListener?.onSuccess(it.body()!!)
                    } else {
                        try {
                            val jObjError = JSONObject(it.errorBody()!!.string())
                            presenterListener?.onFailed(jObjError.getString("error"))
                        } catch (e: JSONException) {
                            presenterListener?.onFailed("unknown error")
                        }
                    }
                }
            }

            override fun onFailure(call: Call<RoomsData>?, t: Throwable?) {
                t.processingFailure(presenterListener)
            }
        })

    }

    private var roomList: MutableList<Room> = mutableListOf()
    private var pages = 0
    override fun startUpdate(
        token: String,
        email: String,
        idProject: String,
        requestType: String,
        searchText: String,
        pages: Int
    ) {
        roomList.clear()
        this.pages = pages
        update(token, email, idProject, requestType, searchText, Constants.DEFAULT_PAGE)
    }

    private fun update(
        token: String,
        email: String,
        idProject: String,
        requestType: String,
        searchText: String,
        page: Int
    ) {
        val call = requestServer.getRooms(
            idProject,
            token,
            email,
            page.toString(),
            requestType,
            searchText
        )

        call.enqueue(object : Callback<RoomsData> {
            override fun onResponse(call: Call<RoomsData>?, response: Response<RoomsData>?) {
                response?.let {
                    if (response.isSuccessful && it.body() != null && page <= pages) {
                        roomList.addAll(it.body()!!.clients.toMutableList())
                        update(token, email, idProject, requestType, searchText, page + 1)
                    } else {
                        it.body()?.let { roomData ->
                            roomData.clients = roomList
                            presenterListener?.onUpdateRoom(roomData)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<RoomsData>?, t: Throwable?) {

            }
        })
    }

    override fun startDeleteRoom(
        index: Int,
        token: String,
        email: String,
        idRoom: String,
        idProject: String
    ) {
        val call = requestServer.deleteDialog(idProject, idRoom, token, email)

        call.enqueue(object : Callback<Request> {
            override fun onResponse(call: Call<Request>?, response: Response<Request>?) {
                response?.let {
                    if (response.isSuccessful && it.body() != null) {
                        presenterListener?.onSuccessDeleteRoom(index)
                    } else {
                        try {
                            val jObjError = JSONObject(it.errorBody()!!.string())
                            presenterListener?.onFailedDeleteRoom(jObjError.getString("error"))
                        } catch (e: JSONException) {
                            presenterListener?.onFailedDeleteRoom("unknown error")
                        }
                    }
                }
            }

            override fun onFailure(call: Call<Request>?, t: Throwable?) {
                t.processingFailure(presenterListener)
            }
        })
    }

    override fun startLoadMenuItems(token: String, email: String) {

        val call = requestServer.getProjects(token, email)

        call.enqueue(object : Callback<List<Project>> {
            override fun onResponse(
                call: Call<List<Project>>?,
                response: Response<List<Project>>?
            ) {
                response?.let {
                    if (response.isSuccessful && it.body() != null) {
                        presenterListener?.onSuccessMenu(it.body()!!)
                    } else {
                        try {
                            val jObjError = JSONObject(it.errorBody()!!.string())
                            presenterListener?.onFailed(jObjError.getString("error"))
                        } catch (e: JSONException) {
                            presenterListener?.onFailed("unknown error")
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<Project>>?, t: Throwable?) {
                t.processingFailure(presenterListener)
            }
        })
    }

    override fun startSendPushId(token: String, login: String, pushId: String) {
        RequestServer
            .create()
            .sendPushId(token, login, pushId)
            .enqueue(object : Callback<Request> {
                override fun onResponse(call: Call<Request>, response: Response<Request>) {
                    //nothing
                }

                override fun onFailure(call: Call<Request>, t: Throwable) {
                    //nothing
                }
            })
    }

    override fun sendSubscribeNotification(
        isSubscribe: Boolean,
        token: String,
        email: String,
        idProject: String
    ) {
        if (isSubscribe) {
            RequestServer
                .create()
                .subscribeToNotifications(idProject, token, email)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        //nothing
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        t.processingFailure(presenterListener)
                    }
                })
        } else {
            RequestServer
                .create()
                .unsubscribeFromNotifications(idProject, token, email)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        //nothing
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        t.processingFailure(presenterListener)
                    }
                })
        }
    }

}
package pro.salebot.mobileclient.mvp

interface BaseView {
    fun onFailed(errorMess: String)
}

interface BasePresenterListener {
    fun onFailed(errorMess: String)
}
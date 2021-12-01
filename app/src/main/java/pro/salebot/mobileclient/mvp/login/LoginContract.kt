package pro.salebot.mobileclient.mvp.login

interface LoginView {
    fun onSuccess(token: String)
    fun onFailed(errorMess: String)
}

interface LoginPresenter {
    fun checkEnter(login: String, token: String)
    fun attemptEnter(login: String, pass: String)
    fun onSuccess(token: String)
    fun onFailed(errorMess: String)
}

interface LoginIterator {
    fun enterUser(login: String, pass: String)
    fun checkAuthUser(login: String, token: String)
}
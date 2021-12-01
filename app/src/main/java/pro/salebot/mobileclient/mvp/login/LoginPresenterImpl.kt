package pro.salebot.mobileclient.mvp.login

class LoginPresenterImpl(
    val loginView: LoginView?
): LoginPresenter {

    val loginIteratorImpl = LoginIteratorImpl(loginView!!)

    override fun attemptEnter(login: String, pass: String) {
        loginIteratorImpl.enterUser(login, pass)
    }

    override fun checkEnter(login: String, token: String) {
        loginIteratorImpl.checkAuthUser(login, token)
    }

    override fun onSuccess(token: String) {
        loginView?.onSuccess(token)
    }

    override fun onFailed(errorMess: String) {
        loginView?.onFailed(errorMess)
    }

}
package pro.salebot.mobileclient.mvp.login


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_login.*
import pro.salebot.mobileclient.R
import pro.salebot.mobileclient.database.DataBaseParams
import pro.salebot.mobileclient.ui.activities.MainContract


class LoginFragment : Fragment(), LoginView {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    private val loginPresenter = LoginPresenterImpl(this)
    private lateinit var dataBaseParams: DataBaseParams

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        progressBarView.visibility = View.GONE
        textErrorView.visibility = View.GONE
        enterView.setOnClickListener {
            startTrans()
            loadAuth()
            loginPresenter.attemptEnter(
                loginView.text.toString(),
                passView.text.toString()
            )
        }

        dataBaseParams = DataBaseParams(context)
        val login : String = dataBaseParams.getKey(DataBaseParams.KEY_LOGIN) ?: ""
        val token : String = dataBaseParams.getKey(DataBaseParams.KEY_TOKEN) ?: ""
        if (!login.isEmpty()) {
            loginView.setText(login)
            if (!token.isEmpty()) {
                loadAuth()
                loginPresenter.checkEnter(login, token)
            }
        }

        createNewView.setOnClickListener {
            val url = "https://salebot.pro/users/sign_up"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        //todo login and pass fill here
//        loginView.setText("1@123.ru")
//        passView.setText("1q2w3e4r5t6y")

    }

    fun loadAuth() {
        createNewView.visibility = View.GONE
        enterView.visibility = View.GONE
        progressBarView.visibility = View.VISIBLE
        passView.visibility = View.GONE
        loginView.isEnabled = false
    }

    override fun onSuccess(token: String) {
        startTrans()
        progressBarView.visibility = View.GONE
        dataBaseParams.setKey(DataBaseParams.KEY_TOKEN, token)
        dataBaseParams.setKey(DataBaseParams.KEY_LOGIN, loginView.text.toString())
        mainContract?.showRooms()
    }

    override fun onFailed(errorMess: String) {
        startTrans()
        progressBarView.visibility = View.GONE
        createNewView.visibility = View.VISIBLE
        enterView.visibility = View.VISIBLE
        passView.visibility = View.VISIBLE
        loginView.isEnabled = true
        textErrorView.visibility = View.VISIBLE
        textErrorView.text = errorMess
        dataBaseParams.setKey(DataBaseParams.KEY_TOKEN, "")
    }

    private fun startTrans() {
        val transContainer = view as ViewGroup?
        transContainer?.let {
            TransitionManager.beginDelayedTransition(transContainer)
        }
    }

    private var mainContract: MainContract? = null

    companion object {
        @JvmStatic
        fun newInstance(mainContract: MainContract) = LoginFragment()
            .apply {
                this.mainContract = mainContract
            }
    }
}

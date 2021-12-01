package pro.salebot.mobileclient.mvp.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ConcatAdapter
import kotlinx.android.synthetic.main.activity_profile.*
import pro.salebot.mobileclient.R
import pro.salebot.mobileclient.database.DataBaseParams
import pro.salebot.mobileclient.mvp.profile.adapter.ButtonAdapter
import pro.salebot.mobileclient.mvp.profile.adapter.LoadMoreAdapter
import pro.salebot.mobileclient.mvp.profile.adapter.ProfileInfoAdapter
import pro.salebot.mobileclient.mvp.profile.adapter.TitleAdapter
import pro.salebot.mobileclient.mvp.profile.data.ProfileInfoData
import pro.salebot.mobileclient.mvp.profile.data.VariablesMo
import pro.salebot.mobileclient.rv.profile.ClientInfoAdapter
import pro.salebot.mobileclient.utils.dialogs.addnewvariable.AddNewVariableDialogFragment
import pro.salebot.mobileclient.utils.dialogs.addnewvariable.ProjectsBottomDialogListener

class ProfileActivity : AppCompatActivity(), ProfileView, ProjectsBottomDialogListener {

    private lateinit var presenter: ProfilePresenter

    companion object {
        private const val EXTRA_CLIENT_ID = "EXTRA_CLIENT_ID"
        private const val EXTRA_PROJECT_ID = "EXTRA_PROJECT_ID"
        private const val EXTRA_NAME = "EXTRA_NAME"
        private const val EXTRA_IMAGE_URL = "EXTRA_IMAGE_URL"

        fun getIntent(
            ctx: Context,
            clientId: String,
            projectId: String,
            name: String,
            imageUrl: String?
        ) = Intent(ctx, ProfileActivity::class.java).apply {
            putExtra(EXTRA_CLIENT_ID, clientId)
            putExtra(EXTRA_PROJECT_ID, projectId)
            putExtra(EXTRA_NAME, name)
            putExtra(EXTRA_IMAGE_URL, imageUrl)
        }
    }

    private lateinit var concatAdapter: ConcatAdapter

    private lateinit var profileInfoAdapter: ProfileInfoAdapter

    private lateinit var titleClientAdapter: TitleAdapter
    private lateinit var titleOrderAdapter: TitleAdapter
    private lateinit var titleUserAdapter: TitleAdapter

    private lateinit var clientInfoAdapter: ClientInfoAdapter
    private lateinit var orderFieldsAdapter: ClientInfoAdapter
    private lateinit var userFieldsAdapter: ClientInfoAdapter

    private lateinit var buttonOrderAdapter: ButtonAdapter
    private lateinit var buttonUserAdapter: ButtonAdapter

    private lateinit var loadMoreAdapter: LoadMoreAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        toolbarView.setNavigationIcon(R.drawable.ic_back)
        toolbarView.setNavigationOnClickListener {
            finish()
        }

        profileInfoAdapter = ProfileInfoAdapter {
            presenter.onClientToMeClicked()
        }

        titleClientAdapter = TitleAdapter(getString(R.string.client_info))
        clientInfoAdapter = ClientInfoAdapter()

        titleUserAdapter = TitleAdapter(getString(R.string.client_value))
        userFieldsAdapter = ClientInfoAdapter { title: String, text: String ->
            presenter.onUserVarItemClicked(
                title,
                text
            )
        }
        buttonUserAdapter = ButtonAdapter(getString(R.string.add_client_value)) {
            presenter.onAddUserVarClicked()
        }

        titleOrderAdapter = TitleAdapter(getString(R.string.order_value))
        orderFieldsAdapter = ClientInfoAdapter { title: String, text: String ->
            presenter.onOrderVarItemClicked(
                title,
                text
            )
        }
        buttonOrderAdapter = ButtonAdapter(getString(R.string.add_order_value)) {
            presenter.onAddOrderVarClicked()
        }

        loadMoreAdapter = LoadMoreAdapter()

        concatAdapter = ConcatAdapter(
            profileInfoAdapter,

            titleClientAdapter,
            clientInfoAdapter,

            titleUserAdapter,
            userFieldsAdapter,
            buttonUserAdapter,

            titleOrderAdapter,
            orderFieldsAdapter,
            buttonOrderAdapter
        )

        rvProfile.adapter = concatAdapter

        presenter = ProfilePresenterImpl(
            ProfileViewModel(
                profileInfoData = ProfileInfoData(
                    imageUrl = intent.getStringExtra(EXTRA_IMAGE_URL)!!,
                    name = intent.getStringExtra(EXTRA_NAME)!!,
                    clientId = intent.getStringExtra(EXTRA_CLIENT_ID)!!
                ),
                projectId = intent.getStringExtra(EXTRA_PROJECT_ID)!!
            ),
            this,
            ProfileModelImpl(
                DataBaseParams(this)
            )
        )
        presenter.onCreate()
    }

    override fun fillProfileInfo(profileInfoData: ProfileInfoData) {
        profileInfoAdapter.data = profileInfoData
        profileInfoAdapter.notifyDataSetChanged()
    }

    override fun showAddNewVariable(tag: VariableDialogTags) {
        showAddNewVariableDialog(
            isChangeVar = false,
            tag = tag
        )
    }

    override fun showChangeVariable(tag: VariableDialogTags, variable: String, value: String) {
        showAddNewVariableDialog(
            isChangeVar = true,
            variable = variable,
            value = value,
            tag = tag
        )
    }

    private fun showAddNewVariableDialog(
        isChangeVar: Boolean,
        variable: String? = null,
        value: String? = null,
        tag: VariableDialogTags
    ) {
        val addNewVariableDialogFragment: AddNewVariableDialogFragment =
            AddNewVariableDialogFragment.newInstance(
                isChangeVar,
                variable,
                value
            )
        addNewVariableDialogFragment.show(
            supportFragmentManager,
            tag.name
        )
    }

    override fun onAddNewClick(tag: String, variable: String, value: String) {
        presenter.onAddNewClicked(tag, variable, value)
    }

    override fun onDeleteClick(tag: String, variable: String) {
        presenter.onDeleteClicked(tag, variable)
    }

    override fun showFailureMessage() {
        Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show()
    }

    override fun addClientInfo(variablesList: List<VariablesMo>) {
        clientInfoAdapter.variablesList = variablesList
        clientInfoAdapter.notifyDataSetChanged()
    }

    override fun updateOrderFields(variablesList: List<VariablesMo>) {
        orderFieldsAdapter.variablesList = variablesList
        orderFieldsAdapter.notifyDataSetChanged()
    }

    override fun updateUserFields(variablesList: List<VariablesMo>) {
        userFieldsAdapter.variablesList = variablesList
        userFieldsAdapter.notifyDataSetChanged()
    }

    override fun changeLoadingVisibility(b: Boolean) {
        if (b) {
            concatAdapter.addAdapter(loadMoreAdapter)

            concatAdapter.removeAdapter(titleClientAdapter)
            concatAdapter.removeAdapter(clientInfoAdapter)

            concatAdapter.removeAdapter(titleUserAdapter)
            concatAdapter.removeAdapter(userFieldsAdapter)
            concatAdapter.removeAdapter(buttonUserAdapter)

            concatAdapter.removeAdapter(titleOrderAdapter)
            concatAdapter.removeAdapter(orderFieldsAdapter)
            concatAdapter.removeAdapter(buttonOrderAdapter)
        } else {
            concatAdapter.removeAdapter(loadMoreAdapter)

            concatAdapter.addAdapter(titleClientAdapter)
            concatAdapter.addAdapter(clientInfoAdapter)

            concatAdapter.addAdapter(titleUserAdapter)
            concatAdapter.addAdapter(userFieldsAdapter)
            concatAdapter.addAdapter(buttonUserAdapter)

            concatAdapter.addAdapter(titleOrderAdapter)
            concatAdapter.addAdapter(orderFieldsAdapter)
            concatAdapter.addAdapter(buttonOrderAdapter)
        }
    }

}
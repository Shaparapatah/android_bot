package pro.salebot.mobileclient.mvp.profile

import pro.salebot.mobileclient.mvp.profile.data.VariablesMo
import java.text.SimpleDateFormat
import java.util.*

class ProfilePresenterImpl(
    private val viewModel: ProfileViewModel,
    private val view: ProfileView,
    private val model: ProfileModel
) : ProfilePresenter {

    override fun onCreate() {
        view.fillProfileInfo(viewModel.profileInfoData)
        view.changeLoadingVisibility(true)
        model.loadUserInfo(
            viewModel.projectId,
            viewModel.profileInfoData.clientId
        ) {
            viewModel.profileInfoData.lastUpdate =
                SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault()).format(it.lastUpdate)
            viewModel.variablesList = it.clientInfoList.map { (title, text) ->
                VariablesMo(title, text)
            }.toMutableList()
            view.addClientInfo(viewModel.variablesList)

            viewModel.orderFields = it.orderFields.map { (title, text) ->
                VariablesMo(title, text)
            }.toMutableList()
            view.updateOrderFields(viewModel.orderFields)

            viewModel.userFields = it.userFields.map { (title, text) ->
                VariablesMo(title, text)
            }.toMutableList()
            view.updateUserFields(viewModel.userFields)

            viewModel.profileInfoData.isMyClient = it.isMyClient

            viewModel.profileInfoData.responsibleId = it.responsibleId

            view.fillProfileInfo(viewModel.profileInfoData)

            view.changeLoadingVisibility(false)
        }
    }

    override fun onClientToMeClicked() {
        if (viewModel.profileInfoData.isMyClient) {
            model.refuseClient(
                viewModel.projectId,
                viewModel.profileInfoData.clientId
            ) { isSuccess ->
                if (isSuccess) {
                    onCreate()
                } else {
                    view.showFailureMessage()
                }
            }
        } else {
            model.takeClient(
                viewModel.projectId,
                viewModel.profileInfoData.clientId
            ) { isSuccess ->
                if (isSuccess) {
                    onCreate()
                } else {
                    view.showFailureMessage()
                }
            }
        }
    }

    override fun onAddOrderVarClicked() {
        view.showAddNewVariable(VariableDialogTags.ORDER)
    }

    override fun onAddUserVarClicked() {
        view.showAddNewVariable(VariableDialogTags.USER)
    }

    override fun onAddNewClicked(tag: String, variable: String, value: String) {
        when (tag) {
            VariableDialogTags.ORDER.name -> {
                model.sendOrderVar(
                    viewModel.projectId,
                    viewModel.profileInfoData.clientId,
                    variable,
                    value
                ) { isSuccess ->
                    if (isSuccess) {
                        viewModel.orderFields.find {
                            it.title == variable
                        }?.let {
                            it.text = value
                        } ?: run {
                            viewModel.orderFields.add(
                                VariablesMo(
                                    variable,
                                    value
                                )
                            )
                        }
                        view.updateOrderFields(viewModel.orderFields)
                    } else {
                        view.showFailureMessage()
                    }
                }
            }
            VariableDialogTags.USER.name -> {
                model.sendUserVar(
                    viewModel.projectId,
                    viewModel.profileInfoData.clientId,
                    variable,
                    value
                ) { isSuccess ->
                    if (isSuccess) {
                        viewModel.userFields.find {
                            it.title == variable
                        }?.let {
                            it.text = value
                        } ?: run {
                            viewModel.userFields.add(
                                VariablesMo(
                                    variable,
                                    value
                                )
                            )
                        }
                        view.updateUserFields(viewModel.userFields)
                    } else {
                        view.showFailureMessage()
                    }
                }
            }
        }
    }

    override fun onDeleteClicked(tag: String, variable: String) {
        when (tag) {
            VariableDialogTags.ORDER.name -> {
                model.deleteOrderVar(
                    viewModel.projectId,
                    viewModel.profileInfoData.clientId,
                    variable
                ) { isSuccess ->
                    if (isSuccess) {
                        viewModel.orderFields.let {
                            it.remove(it.find { variableMo -> variableMo.title == variable })
                        }
                        view.updateOrderFields(viewModel.orderFields)
                    } else {
                        view.showFailureMessage()
                    }
                }
            }
            VariableDialogTags.USER.name -> {
                model.deleteUserVar(
                    viewModel.projectId,
                    viewModel.profileInfoData.clientId,
                    variable
                ) { isSuccess ->
                    if (isSuccess) {
                        viewModel.userFields.let {
                            it.remove(it.find { variableMo -> variableMo.title == variable })
                        }
                        view.updateUserFields(viewModel.userFields)
                    } else {
                        view.showFailureMessage()
                    }
                }
            }
        }
    }

    override fun onOrderVarItemClicked(variable: String, value: String) {
        view.showChangeVariable(
            VariableDialogTags.ORDER,
            variable,
            value
        )
    }

    override fun onUserVarItemClicked(variable: String, value: String) {
        view.showChangeVariable(
            VariableDialogTags.USER,
            variable,
            value
        )
    }

}
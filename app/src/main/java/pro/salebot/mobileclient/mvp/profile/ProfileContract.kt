package pro.salebot.mobileclient.mvp.profile

import pro.salebot.mobileclient.mvp.profile.data.ProfileInfoData
import pro.salebot.mobileclient.mvp.profile.data.VariablesMo

interface ProfileView {
    fun addClientInfo(variablesList: List<VariablesMo>)
    fun updateOrderFields(variablesList: List<VariablesMo>)
    fun updateUserFields(variablesList: List<VariablesMo>)
    fun showAddNewVariable(tag: VariableDialogTags)
    fun showChangeVariable(tag: VariableDialogTags, variable: String, value: String)
    fun changeLoadingVisibility(b: Boolean)
    fun showFailureMessage()
    fun fillProfileInfo(profileInfoData: ProfileInfoData)
}

interface ProfilePresenter {
    fun onCreate()
    fun onClientToMeClicked()
    fun onAddOrderVarClicked()
    fun onAddUserVarClicked()
    fun onAddNewClicked(tag: String, variable: String, value: String)
    fun onDeleteClicked(tag: String, variable: String)
    fun onOrderVarItemClicked(variable: String, value: String)
    fun onUserVarItemClicked(variable: String, value: String)
}
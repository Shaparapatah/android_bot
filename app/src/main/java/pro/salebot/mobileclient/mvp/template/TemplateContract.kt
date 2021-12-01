package pro.salebot.mobileclient.mvp.template

import pro.salebot.mobileclient.mvp.template.entity.TemplateItem

interface TemplateView {
    fun showFailureMessage()
    fun goToBackWithResult(id: Int, answer: String)
    fun showLoading(isVisible: Boolean)
    fun addTemplateList(templates: List<TemplateItem>)
    fun changeShowAllVisibility(isVisible: Boolean)
    fun clearColorsCheck()
}

interface TemplatePresenter {
    fun onCreate()
    fun onChipClicked(id: Int, answer: String)
    fun onColorCheckedChange(messageType: Int?)
    fun onSearchClick(text: String)
    fun onShowAllClick()
}
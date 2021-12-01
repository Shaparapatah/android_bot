package pro.salebot.mobileclient.mvp.template

import pro.salebot.mobileclient.mvp.template.entity.MessageType
import pro.salebot.mobileclient.mvp.template.entity.MessagesBlocksMo
import pro.salebot.mobileclient.mvp.template.entity.TemplateItem

class TemplatePresenterImpl(
    private val viewModel: TemplateViewModel,
    private val view: TemplateView,
    private val model: TemplateModel
) : TemplatePresenter {

    private val messagesBlockListener: (MessagesBlocksMo?) -> Unit = {
        if (it != null) {
            view.addTemplateList(
                it.blocks?.map { blockMo ->
                    TemplateItem(
                        blockMo.id,
                        blockMo.answer,
                        blockMo.messageType ?: MessageType.COLOR1
                    )
                }.orEmpty()
            )
        } else {
            view.showFailureMessage()
        }
        view.showLoading(false)
    }

    override fun onCreate() {
        view.showLoading(true)
        model.getMessageBlocks(
            viewModel.projectId,
            viewModel.messageType?.toString(),
            viewModel.searchText,
            cb = messagesBlockListener
        )
    }

    override fun onColorCheckedChange(messageType: Int?) {
        viewModel.messageType = messageType
        view.changeShowAllVisibility(true)
        requestMessageBlocks()
    }

    override fun onSearchClick(text: String) {
        viewModel.searchText = text
        requestMessageBlocks()
    }

    override fun onShowAllClick() {
        viewModel.messageType = null
        view.clearColorsCheck()
        view.changeShowAllVisibility(false)
        requestMessageBlocks()
    }

    private fun requestMessageBlocks() {
        view.showLoading(true)
        model.getMessageBlocks(
            viewModel.projectId,
            viewModel.messageType?.toString(),
            viewModel.searchText,
            cb = messagesBlockListener
        )
    }

    override fun onChipClicked(id: Int, answer: String) {
        view.goToBackWithResult(id, answer)
    }

}
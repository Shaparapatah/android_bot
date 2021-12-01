package pro.salebot.mobileclient.mvp.template

data class TemplateViewModel(
    val projectId: String,
    var messageType: Int? = null,
    var searchText: String? = null
)
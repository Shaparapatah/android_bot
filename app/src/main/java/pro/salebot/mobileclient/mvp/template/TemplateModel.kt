package pro.salebot.mobileclient.mvp.template

import pro.salebot.mobileclient.mvp.template.entity.MessagesBlocksMo

interface TemplateModel {

    fun getMessageBlocks(
        projectId: String,
        type: String? = null,
        search: String? = null,
        cb: (MessagesBlocksMo?) -> Unit
    )

}
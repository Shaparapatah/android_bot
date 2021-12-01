package pro.salebot.mobileclient

import androidx.annotation.StringRes

class Constants {

    companion object {
        const val EXTRA_ID_ROOM = "EXTRA_ID_ROOM"
        const val EXTRA_ID_PROJECT = "EXTRA_ID_PROJECT"
        const val EXTRA_NAME_ROOM = "EXTRA_NAME_ROOM"
        const val EXTRA_IMAGE_ROOM = "EXTRA_IMAGE_ROOM"
        const val EXTRA_CLIENT_TYPE = "EXTRA_CLIENT_TYPE"
        const val EXTRA_USERS_COUNTS = "EXTRA_USERS_COUNTS"
        const val EXTRA_PUSH_MESSAGE = "message"

        const val DEFAULT_ID = -1
        const val DEFAULT_PAGE = 1
    }

    enum class ClientType(@StringRes val nameRes: Int) {
        VK_TYPE(R.string.client_type_vk),
        TG_TYPE(R.string.client_type_telegram),
        VB_TYPE(R.string.client_type_viber),
        FB_TYPE(R.string.client_type_facebook),
        TALK_ME_TYPE(R.string.client_type_talk_me),
        ONLINE_CHAT_TYPE(R.string.client_type_online_chat),
        WHATSAPP_TYPE(R.string.client_type_whatsapp),
        AVITO_TYPE(R.string.client_type_avito),
        OK_TYPE(R.string.client_type_ok),
        RETAIL_TYPE(R.string.client_type_retail),
        INSTAGRAM_TYPE(R.string.client_type_instagram);

        companion object {
            fun getTypeResString(type: Int) =
                ClientType.values().find { it.ordinal == type }?.nameRes ?: -1
        }
    }
}

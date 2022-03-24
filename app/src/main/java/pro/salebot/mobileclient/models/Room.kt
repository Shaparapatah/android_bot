package pro.salebot.mobileclient.models

import com.google.gson.annotations.SerializedName
import java.util.*

data class RoomsData(
    val all: Int,
    val free: Int,
    val wait: Int,
    val my: Int,
    @SerializedName("users_count") val usersCount: Int,
    var clients: List<Room>
)

data class Room(
    var id: String,
    var client_type: Int,
    var name: String = "",
    var log: String = "",
    var avatar: String = "",
    var created_at: Date,
    var updated_at: Date,
    var last_message: Message?,
    var answered: Boolean,
    /** Добавил поле 22.03.2022 */
    var selected : Boolean


) {


    fun getMessages() : List<Message> {
        val messages = mutableListOf<Message>()

//        try {
//            val jsonArray = JSONArray(log)
//            for (i in 0 until jsonArray.length()) {
//                val jo = jsonArray.getJSONObject(i)
//                if (jo.getString("text").isNotEmpty())
//                    messages.add(
//                        Message(
//                            jo.getBoolean("client_replica"),
//                            id.toInt(),
//                            jo.getString("text"),
//                            Date(jo.getLong("time") * 1000)
//                        )
//                    )
//            }
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        } catch (e: NullPointerException) {
//            e.printStackTrace()
//        }

        return messages
    }

}

package pro.salebot.mobileclient.models

data class Project(
    val id: String,
    val name: String,
    var check: Boolean = false
)
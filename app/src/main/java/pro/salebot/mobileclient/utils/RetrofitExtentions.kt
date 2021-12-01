package pro.salebot.mobileclient.utils

import pro.salebot.mobileclient.mvp.BasePresenterListener
import retrofit2.Response


fun <T> Response<T>?.processingResponse(listener: BasePresenterListener?, onSuccess: (T) -> Unit) {
    this?.let {
        if (it.isSuccessful && it.body() != null) {
            onSuccess.invoke(this.body()!!)
        } else {
            listener?.onFailed("Error " + it.errorBody()?.contentType().toString())
        }
    }
}

fun Throwable?.processingFailure(listener: BasePresenterListener?) {
    listener?.onFailed(this?.localizedMessage ?: "Error")
}
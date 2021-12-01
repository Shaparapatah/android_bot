package pro.salebot.mobileclient.api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import pro.salebot.mobileclient.api.models.Request
import pro.salebot.mobileclient.api.models.UserStructure
import pro.salebot.mobileclient.models.*
import pro.salebot.mobileclient.mvp.profile.data.ClientInfoResp
import pro.salebot.mobileclient.mvp.template.entity.MessagesBlocksMo
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface RequestServer {

    companion object {
        private const val BASE_URL = "https://salebot.pro"
        fun create(): RequestServer {

            val httpClient = OkHttpClient()
//            httpClient.networkInterceptors().add(object : Interceptor {
//                @Throws(IOException::class)
//                override fun intercept(chain: Interceptor.Chain): Response {
//                    val requestBuilder = chain.request().newBuilder()
//                    requestBuilder.header("Content-Type", "application/json")
//                    return chain.proceed(requestBuilder.build())
//                }
//            })
            val gson = GsonBuilder()
                .setLenient()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create()

            val retrofit = Retrofit.Builder()
                .client(httpClient)
//                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create(gson))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(BASE_URL)
                .build()

            return retrofit.create(RequestServer::class.java)
        }
    }

    @GET("/")
    fun auth(
        @Query("email") email: String,
        @Query("authentication_token") token: String
    ): Call<String>

    @Headers("Content-Type: application/json")
    @POST("/users/sign_in.json")
    fun enter(
//        @Header("Accept") headerAccept: String,
//        @Header("Content-Type") headerContentType: String,
//        @FieldMap map : Map<String, User>
//        @Field("User") user: User
//        @Body data: String
//        @Field("user") user: UserStructure
        @Body user: UserStructure
//        @FieldMap map : Map<String, String>
//        @Field("user") user: group.bost.mobile_client.api.models.User
//        @Field("email") email: String,
//        @Field("password") password: String
    ): Call<pro.salebot.mobileclient.models.User>

    @GET("/mobile_api_projects")
    fun getProjects(
        @Query("user_token") token: String,
        @Query("user_email") email: String
    ): Call<List<Project>>

    @GET("/mobile_api_v2/{id}/clients")
    fun getRooms(
        @Path("id") idProject: String,
        @Query("user_token") token: String,
        @Query("user_email") email: String,
        @Query("requestType") requestType: String, // "ALL", "WAIT", "MY", "FREE"
        @Query("searchText") searchText: String?
    ): Call<RoomsData>

    @GET("/mobile_api_v2/{id}/clients")
    fun getRooms(
        @Path("id") idProject: String,
        @Query("user_token") token: String,
        @Query("user_email") email: String,
        @Query("page") page: String,
        @Query("requestType") requestType: String, // "ALL", "WAIT", "MY", "FREE"
        @Query("searchText") searchText: String?
    ): Call<RoomsData>

    @POST("/mobile_api/{id}/new_message")
    @FormUrlEncoded
    fun sendMessage(
        @Path("id") idProject: String,
        @Field("client_id") idRoom: String,
        @Field("user_token") token: String,
        @Field("user_email") email: String,
        @Field("text") text: String,
        @Field("message_id") messageId: String?
    ): Call<Request>

    @DELETE("/mobile_api/{project_id}/client/{room_id}")
    fun deleteDialog(
        @Path("project_id") idProject: String,
        @Path("room_id") idRoom: String,
        @Query("user_token") token: String,
        @Query("user_email") email: String
    ): Call<Request>

    @Headers("Content-Type: application/json")
    @GET("/mobile_api_v2/{project_id}/client/{room_id}.json")
    fun getMessages(
        @Path("project_id") idProject: String,
        @Path("room_id") idRoom: String,
        @Query("user_token") token: String,
        @Query("user_email") email: String,
        @Query("page") page: Int
    ): Call<MessageData>

    @POST("/mobile_api_v2/{project_id}/block_client/{room_id}")
    @FormUrlEncoded
    fun blockClient(
        @Path("project_id") idProject: String,
        @Path("room_id") idRoom: String,
        @Field("user_token") token: String,
        @Field("user_email") email: String
    ): Call<ClientStatus>

    @POST("/mobile_api_v2/{project_id}/pause_client/{room_id}")
    @FormUrlEncoded
    fun pauseBot(
        @Path("project_id") idProject: String,
        @Path("room_id") idRoom: String,
        @Field("user_token") token: String,
        @Field("user_email") email: String
    ): Call<BotStatus>

    @POST("/mobile_api/set_apple_device_id")
    @FormUrlEncoded
    fun sendPushId(
        @Field("user_token") token: String,
        @Field("user_email") email: String,
        @Field("apple_device_id") pushId: String
    ): Call<Request>

    @GET("/mobile_api_v2/{project_id}/profile/{client_id}")
    fun getProfileInfo(
        @Path("project_id") projectId: String,
        @Path("client_id") clientId: String,
        @Query("user_token") token: String,
        @Query("user_email") email: String
    ): Call<ClientInfoResp>

    @POST("/mobile_api_v2/{project_id}/change_order_var/{client_id}")
    @FormUrlEncoded
    fun changeOrderVar(
        @Field("user_token") token: String,
        @Field("user_email") email: String,
        @Path("project_id") projectId: String,
        @Path("client_id") clientId: String,
        @Field("name") name: String,
        @Field("value") value: String
    ): Call<ResponseBody>

    @POST("/mobile_api_v2/{project_id}/user_order_var/{client_id}")
    @FormUrlEncoded
    fun changeUserVar(
        @Field("user_token") token: String,
        @Field("user_email") email: String,
        @Path("project_id") projectId: String,
        @Path("client_id") clientId: String,
        @Field("name") name: String,
        @Field("value") value: String
    ): Call<ResponseBody>

    @DELETE("/mobile_api_v2/{project_id}/delete_order_var/{client_id}")
    fun deleteOrderVar(
        @Path("project_id") projectId: String,
        @Path("client_id") clientId: String,
        @Query("user_token") token: String,
        @Query("user_email") email: String,
        @Query("name") variable: String
    ): Call<ResponseBody>

    @DELETE("/mobile_api_v2/{project_id}/delete_user_var/{client_id}")
    fun deleteUserVar(
        @Path("project_id") projectId: String,
        @Path("client_id") clientId: String,
        @Query("user_token") token: String,
        @Query("user_email") email: String,
        @Query("name") variable: String
    ): Call<ResponseBody>

    @POST("/mobile_api_v2/{project_id}/take_client/{client_id}")
    @FormUrlEncoded
    fun takeClient(
        @Path("project_id") projectId: String,
        @Path("client_id") clientId: String,
        @Field("user_token") token: String,
        @Field("user_email") email: String
    ): Call<ResponseBody>

    @POST("/mobile_api_v2/{project_id}/refuse_client/{client_id}")
    @FormUrlEncoded
    fun refuseClient(
        @Path("project_id") projectId: String,
        @Path("client_id") clientId: String,
        @Field("user_token") token: String,
        @Field("user_email") email: String
    ): Call<ResponseBody>

    @GET("mobile_api_v2/{project_id}/messages_blocks")
    fun getMessagesBlocks(
        @Path("project_id") projectId: String,
        @Query("user_token") token: String,
        @Query("user_email") email: String,
        @Query("type") type: String?,
        @Query("search") search: String?
    ): Call<MessagesBlocksMo>

    @GET("mobile_api/{project_id}/subscribe_to_notifications")
    fun subscribeToNotifications(
        @Path("project_id") projectId: String,
        @Query("user_token") token: String,
        @Query("user_email") email: String
    ): Call<ResponseBody>

    @GET("mobile_api/{project_id}/unsubscribe_from_notifications")
    fun unsubscribeFromNotifications(
        @Path("project_id") projectId: String,
        @Query("user_token") token: String,
        @Query("user_email") email: String
    ): Call<ResponseBody>

}
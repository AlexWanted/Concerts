package ru.rewindforce.concerts.utils

import android.util.Log
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import ru.rewindforce.concerts.data.*
import java.util.concurrent.TimeUnit.SECONDS

private const val HOST: String = "https://rocky-springs-19680.herokuapp.com"

open class Model {

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
                                                 .writeTimeout(30, SECONDS)
                                                 .readTimeout(30, SECONDS)
                                                 .build()

    protected val retrofit: Retrofit = Retrofit.Builder()
                                     .baseUrl(HOST)
                                     .client(okHttpClient)
                                     .addConverterFactory(GsonConverterFactory.create())
                                     .build()

    fun onDestroy() {
        okHttpClient.dispatcher().cancelAll()
    }

    fun<T> Call<T>.enqueue(callback: ResponseCallback) {
        val callBackKt = ConcertsCallBack<T>(callback)
        this.enqueue(callBackKt)
    }

    class ConcertsCallBack<T>(private val callback: ResponseCallback): Callback<T> {
        override fun onFailure(call: Call<T>, t: Throwable) {
            callback.onError()
            Log.e("API ERROR", t.message)
        }

        override fun onResponse(call: Call<T>, response: Response<T>) {
            callback.onResponse(response.body(), response.code())
        }

    }

    interface ResponseCallback {
        fun<T> onResponse(response: T, code: Int)
        fun onError()
    }

    protected interface ProfileApi {
        @GET("/user")
        fun getUserInfo(@Header("Authorization") token: String?,
                        @Header("UID") user_uid: String?,
                        @Query("login") login: String?): Call<User>

        @GET("/user/{user_uid}/friends")
        fun getFriendsList(@Header("Authorization") token: String?,
                           @Header("UID") auth_uid: String?,
                           @Path("user_uid") user_uid: String?): Call<ArrayList<User>>

        @GET("/user/requests/incoming")
        fun getIncomingRequests(@Header("Authorization") token: String,
                                @Header("UID") user_uid: String): Call<ArrayList<User>>

        @GET("/user/requests/outgoing")
        fun getOutgoingRequests(@Header("Authorization") token: String,
                                @Header("UID") user_uid: String): Call<ArrayList<User>>

        @GET("/user/{user_uid}/requests/count")
        fun getRequestsCount(@Header("Authorization") token: String?,
                           @Path("user_uid") user_uid: String?): Call<ResponseBody>

        @Multipart
        @PATCH("/user")
        fun patchUser(@Header("Authorization") token: String?,
                      @Part("uuid") uuid: RequestBody?,
                      @Part("user_firstname") user_firstname: RequestBody?,
                      @Part("user_lastname") user_lastname: RequestBody?,
                      @Part avatar: MultipartBody.Part?,
                      @Part header: MultipartBody.Part?): Call<ResponseBody>

        @POST("/user/{receiver_uid}/add")
        fun addFriend(@Path("receiver_uid") receiver_uid: String?,
                      @Header("Authorization") token: String?,
                      @Header("UID") user_uid: String?): Call<ResponseBody>

        @DELETE("/user/{receiver_uid}/delete")
        fun deleteFriend(@Path("receiver_uid") receiver_uid: String?,
                         @Header("Authorization") token: String?,
                         @Header("UID") user_uid: String?): Call<ResponseBody>

        @DELETE("/user/{receiver_uid}/cancel")
        fun cancelRequest(@Path("receiver_uid") receiver_uid: String?,
                          @Header("Authorization") token: String?,
                          @Header("UID") user_uid: String?): Call<ResponseBody>

        @DELETE("/user/{sender_uid}/reject")
        fun rejectRequest(@Path("sender_uid") sender_uid: String?,
                          @Header("Authorization") token: String?,
                          @Header("UID") user_uid: String?): Call<ResponseBody>

        @POST("/user/{sender_uid}/accept")
        fun acceptRequest(@Path("sender_uid") sender_uid: String?,
                          @Header("Authorization") token: String?,
                          @Header("UID") user_uid: String?): Call<ResponseBody>

        @GET("/user/{page_uid}/post/get")
        fun getPosts(@Path("page_uid") page_uid: String,
                     @Header("Authorization") token: String,
                     @Header("UID") user_uid: String): Call<ArrayList<Post>>
    }

    internal interface ConcertsApi {
        @GET("/concert")
        fun getData(@Query("limit") limit: Int, @Query("offset") offset: Int,
                    @Query("order_by") order_by: String, @Query("asc_desc") asc_desc: String): Call<ArrayList<Concert>>

        @GET("/user/{user_uid}/concertslist")
        fun getWishlist(@Header("Authorization") token: String,
                        @Header("UID") viewer_uid: String,
                        @Path("user_uid") user_uid: String,
                        @Query("limit") limit: Int,
                        @Query("offset") offset: Int,
                        @Query("asc_desc") asc_desc: String): Call<ArrayList<Concert>>

        @POST("/concertslist")
        fun putToWishlist(@Header("Authorization") token: String,
                          @Header("UID") user_uid: String,
                          @Query("concert_id") concert_id: String): Call<ResponseBody>

        @DELETE("/concertslist")
        fun deleteFromWishlist(@Header("Authorization") token: String,
                               @Header("UID") user_uid: String,
                               @Query("concert_id") concert_id: String): Call<ResponseBody>

        @GET("/concertslist")
        fun getStatus(@Header("Authorization") token: String,
                      @Header("UID") user_uid: String,
                      @Query("concert_id") concert_id: String): Call<ResponseBody>

        @GET("/band")
        fun getLineUp(@Query("line_up[]") bands_id: ArrayList<String>): Call<ArrayList<Band>>

        @GET("/comments/concert/{concert_id}")
        fun getComments(@Path("concert_id") concert_id: String,
                        @Query("limit") limit: Int,
                        @Query("offset") offset: Int): Call<ArrayList<Comment>>

        @Multipart
        @POST("/comments/concert/{uid}")
        fun postComment(@Path("uid") concert_id: String,
                        @Header("Authorization") token: String,
                        @Header("UID") user_uid: String,
                        @Part("message") message: RequestBody): Call<Comment>

        @POST("/comments/concert/{uid}/{comment_uid}/like")
        fun likeComment(@Path("uid") concert_id: String,
                        @Path("comment_uid") comment_id: String,
                        @Header("Authorization") token: String,
                        @Header("UID") user_uid: String): Call<ResponseBody>

        @DELETE("/comments/concert/{uid}/{comment_uid}/dislike")
        fun dislikeComment(@Path("uid") concert_id: String,
                           @Path("comment_uid") comment_id: String,
                           @Header("Authorization") token: String,
                           @Header("UID") user_uid: String): Call<ResponseBody>

        @DELETE("/comments/concert/{concert_uid}/{comment_uid}")
        fun deleteComment(@Path("concert_uid") concert_id: String,
                          @Path("comment_uid") comment_id: String,
                          @Header("Authorization") token: String,
                          @Header("UID") user_uid: String): Call<ResponseBody>

        @Multipart
        @PATCH("/comments/concert/{concert_uid}/{comment_uid}")
        fun editComment(@Path("concert_uid") concert_id: String,
                        @Path("comment_uid") comment_id: String,
                        @Header("Authorization") token: String,
                        @Header("UID") user_uid: String,
                        @Part("message") message: RequestBody): Call<ResponseBody>
    }

}

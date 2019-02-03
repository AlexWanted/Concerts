package ru.rewindforce.concerts.details

import okhttp3.RequestBody
import ru.rewindforce.concerts.utils.Model
import java.util.ArrayList

class ConcertDetailsModel: Model() {
    private val concertDetailsApi: Model.ConcertsApi = retrofit.create(Model.ConcertsApi::class.java)

    fun putToWishlist(args: Map<String, Any?>, callback: ResponseCallback) {
        val token: String by args
        val authUID: String by args
        val concertUID: String by args
        concertDetailsApi.putToWishlist(token, authUID, concertUID).enqueue(callback)
    }

    fun deleteFromWishlist(args: Map<String, Any?>, callback: ResponseCallback) {
        val token: String by args
        val authUID: String by args
        val concertUID: String by args
        concertDetailsApi.deleteFromWishlist(token, authUID, concertUID).enqueue(callback)
    }

    fun getStatus(args: Map<String, Any?>, callback: ResponseCallback) {
        val token: String by args
        val userUID: String by args
        val concertUID: String by args
        concertDetailsApi.getStatus(token, userUID, concertUID).enqueue(callback)
    }

    fun getLineUp(args: Map<String, Any?>, callback: ResponseCallback) {
        val bandsUID: ArrayList<String> by args
        concertDetailsApi.getLineUp(bandsUID).enqueue(callback)
    }

    fun getComments(args: Map<String, Any?>, callback: ResponseCallback) {
        val concertUID: String by args
        val limit: Int by args
        val offset: Int by args
        concertDetailsApi.getComments(concertUID, limit, offset).enqueue(callback)
    }

    fun sendComment(args: Map<String, Any?>, callback: ResponseCallback) {
        val concertUID: String by args
        val token: String by args
        val userUID: String by args
        val message: RequestBody by args
        concertDetailsApi.postComment(concertUID, token, userUID, message).enqueue(callback)
    }

    fun likeComment(args: Map<String, Any?>, callback: ResponseCallback) {
        val concertUID: String by args
        val commentUID: String by args
        val token: String by args
        val userUID: String by args
        concertDetailsApi.likeComment(concertUID, commentUID, token, userUID).enqueue(callback)
    }

    fun dislikeComment(args: Map<String, Any?>, callback: ResponseCallback) {
        val concertUID: String by args
        val commentUID: String by args
        val token: String by args
        val userUID: String by args
        concertDetailsApi.dislikeComment(concertUID, commentUID, token, userUID).enqueue(callback)
    }

    fun deleteComment(args: Map<String, Any?>, callback: ResponseCallback) {
        val concertUID: String by args
        val commentUID: String by args
        val token: String by args
        val userUID: String by args
        concertDetailsApi.deleteComment(concertUID, commentUID, token, userUID).enqueue(callback)
    }

    fun editComment(args: Map<String, Any?>, callback: ResponseCallback) {
        val concertUID: String by args
        val commentUID: String by args
        val token: String by args
        val userUID: String by args
        val message: RequestBody by args
        concertDetailsApi.editComment(concertUID, commentUID, token, userUID, message).enqueue(callback)
    }
}
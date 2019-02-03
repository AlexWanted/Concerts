package ru.rewindforce.concerts.details

import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import ru.rewindforce.concerts.data.Band
import ru.rewindforce.concerts.data.Comment
import ru.rewindforce.concerts.utils.Presenter
import java.util.ArrayList

class ConcertDetailsPresenter: Presenter<ConcertDetailsModel>() {
    init { this.model = ConcertDetailsModel() }

    fun getStatus(token: String, userUID: String, concertUID: String) {
        val args = mapOf("token" to token, "userUID" to userUID, "concertUID" to concertUID)
        model.getStatus(args, callback(
                onResponse = { _: Any, code: Int -> when (code) {
                    200 -> fragment?.onStatusLoad("going")
                    404 -> fragment?.onStatusLoad("not going")
                    204 -> fragment?.onError()
                }},
                onFailure = { fragment?.onError() }
        ))
    }

    fun getLineUp(bandsUID: ArrayList<String>) {
        val args = mapOf("bandsUID" to bandsUID)
        model.getLineUp(args, callback(
                onResponse = {body: ArrayList<Band>, _ -> fragment?.onLineUpLoad(body) },
                onFailure = { fragment?.onError() }
        ))
    }

    fun putToWishlist(buttonID: Int, token: String, userUID: String, state: String, concertUID: String) {
        val args = mapOf("token" to token, "authUID" to userUID, "concertUID" to concertUID)

        val callback = callback(onResponse = {_: Any, _ -> fragment?.onConcertsLoad(buttonID) },
                                onFailure = { fragment?.onError() })

        when (state) {
            "going", "maybe" -> model.putToWishlist(args, callback)
            "not going" -> model.deleteFromWishlist(args, callback)
        }
    }

    fun getComments(concertUID: String, limit: Int, offset: Int) {
        val args = mapOf("concertUID" to concertUID, "limit" to limit, "offset" to offset)
        model.getComments(args, callback(
                onResponse = {body: ArrayList<Comment>, _ -> fragment?.onCommentsLoad(body) },
                onFailure = { fragment?.onCommentsLoad(ArrayList()) }))
    }

    fun sendComment(concertUID: String, token: String, userUID: String, message: String) {
        val partMessage = RequestBody.create(MediaType.parse("text/plain"), message)
        val args = mapOf("concertUID" to concertUID, "token" to token, "userUID" to userUID, "message" to partMessage)
        model.sendComment(args, callback(
                onResponse = {body: Comment, _ -> fragment?.onCommentPosted(body) },
                onFailure = { fragment?.onCommentError() }))
    }

    fun likeComment(concertUID: String, commentUID: String, token: String, userUID: String, adapterPos: Int) {
        val args = mapOf("concertUID" to concertUID, "commentUID" to commentUID, "token" to token, "userUID" to userUID)
        model.likeComment(args, callback(
                onResponse = {_: Any, _ -> fragment?.onLiked(adapterPos) },
                onFailure = { fragment?.onError() }))
    }

    fun dislikeComment(concertUID: String, commentUID: String, token: String, userUID: String, adapterPos: Int) {
        val args = mapOf("concertUID" to concertUID, "commentUID" to commentUID, "token" to token, "userUID" to userUID)
        model.dislikeComment(args, callback(
                onResponse = {_: Any, _ -> fragment?.onDisliked(adapterPos) },
                onFailure = { fragment?.onError() }))
    }

    fun deleteComment(concertUID: String, commentUID: String, token: String, userUID: String, adapterPos: Int) {
        val args = mapOf("concertUID" to concertUID, "commentUID" to commentUID, "token" to token, "userUID" to userUID)
        model.deleteComment(args, callback(
                onResponse = {_: Any, _ -> fragment?.onCommentDeleted(adapterPos) },
                onFailure = { fragment?.onError() }))
    }

    fun editComment(message: String, concertUID: String, commentUID: String, token: String,
                    userUID: String, adapterPos: Int) {
        val partMessage = RequestBody.create(MediaType.parse("text/plain"), message)
        val args = mapOf("concertUID" to concertUID, "commentUID" to commentUID, "token" to token,
                         "userUID" to userUID, "message" to partMessage)
        model.editComment(args, callback(
                onResponse = {body: ResponseBody, _ -> fragment?.onCommentEdited(body.string(), adapterPos) },
                onFailure = { fragment?.onError() }))
    }
}
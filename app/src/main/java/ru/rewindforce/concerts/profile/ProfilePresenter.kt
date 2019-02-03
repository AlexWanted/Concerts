package ru.rewindforce.concerts.profile

import okhttp3.ResponseBody
import ru.rewindforce.concerts.data.Post
import ru.rewindforce.concerts.data.User
import ru.rewindforce.concerts.utils.BaseFragment
import ru.rewindforce.concerts.utils.Presenter

class ProfilePresenter(private val cFragment: BaseFragment?): Presenter<ProfileModel>(cFragment) {
    init { this.model = ProfileModel() }

    fun getFriendsList(token: String?, authUID: String?, userUID: String?) {
        val args = mapOf("token" to token, "authUID" to authUID, "userUID" to userUID)
        model.getFriendsList(args, callback(
                onResponse = {body: ArrayList<User>, _ -> cFragment?.onUsersLoad(body)},
                onFailure = { cFragment?.onError() }
        ))
    }

    fun getIncomingRequests(token: String?, userUID: String?) {
        val args = mapOf("token" to token, "userUID" to userUID)
        model.getIncomingRequests(args, callback(
                onResponse = {body: ArrayList<User>, _ -> cFragment?.onUsersLoad(body)},
                onFailure = { cFragment?.onError() }
        ))
    }

    fun getOutgoingRequests(token: String?, userUID: String?) {
        val args = mapOf("token" to token, "userUID" to userUID)
        model.getOutgoingRequests(args, callback(
                onResponse = {body: ArrayList<User>, _ -> cFragment?.onUsersLoad(body)},
                onFailure = { cFragment?.onError() }
        ))
    }

    fun getRequestsCount(token: String?, userUID: String?) {
        val args = mapOf("token" to token, "userUID" to userUID)
        model.getRequestsCount(args, callback(
                onResponse = {body: ResponseBody, _ ->
                    cFragment?.onRequestsCount(Regex("[^0-9]").replace(body.string(), "").toInt()) },
                onFailure = { cFragment?.onError() }
        ))
    }

    fun getProfileInfo(token: String?, userUID: String?, login: String?) {
        val args = mapOf("token" to token, "userUID" to userUID, "login" to login)
        model.getProfileInfo(args, callback(
                onResponse = {body: User, _ -> cFragment?.onProfileInfo(body)},
                onFailure = { cFragment?.onError() }
        ))
    }

    fun addFriend(token: String?, sender_uid: String?, receiver_uid: String?) {
        val args = mapOf("token" to token, "senderUID" to sender_uid, "receiverUID" to receiver_uid)
        model.addFriend(args, callback(
                onResponse = {_: Any, code: Int ->
                    when (code) {
                        200 -> cFragment?.onRequestSent()
                        else -> cFragment?.onError()
                    }
                },
                onFailure = { cFragment?.onError() }
        ))
    }

    fun deleteFriend(token: String?, sender_uid: String?, receiver_uid: String?) {
        val args = mapOf("token" to token, "senderUID" to sender_uid, "receiverUID" to receiver_uid)
        model.deleteFriend(args, callback(
                onResponse = {_: Any, code: Int ->
                    when (code) {
                        200 -> cFragment?.onFriendDelete()
                        else -> cFragment?.onError()
                    }
                },
                onFailure = { cFragment?.onError() }
        ))
    }

    fun cancelRequest(token: String?, sender_uid: String?, receiver_uid: String?, pos: Int = -1) {
        val args = mapOf("token" to token, "senderUID" to sender_uid, "receiverUID" to receiver_uid)
        model.cancelRequest(args, callback(
                onResponse = {_: Any, code: Int ->
                        when (code) {
                        200 -> cFragment?.onFriendDelete(pos)
                        else -> cFragment?.onError()
                    }
                },
                onFailure = { cFragment?.onError() }
        ))
    }

    fun rejectRequest(token: String?, receiver_uid: String?, sender_uid: String?, pos: Int = -1) {
        val args = mapOf("token" to token, "senderUID" to sender_uid, "receiverUID" to receiver_uid)
        model.rejectRequest(args, callback(
                onResponse = {_: Any, code: Int ->
                    when (code) {
                        200 -> cFragment?.onFriendDelete(pos)
                        else -> cFragment?.onError()
                    }
                },
                onFailure = { cFragment?.onError() }
        ))
    }

    fun acceptRequest(token: String?, receiver_uid: String?, sender_uid: String?, pos: Int = -1) {
        val args = mapOf("token" to token, "senderUID" to sender_uid, "receiverUID" to receiver_uid)
        model.acceptRequest(args, callback(
                onResponse = {_: Any, code: Int ->
                    when (code) {
                        200 -> cFragment?.onFriendAdded(pos)
                        else -> cFragment?.onError()
                    }
                },
                onFailure = { cFragment?.onError() }
        ))
    }

    fun getPosts(token: String, userUID: String, pageUID: String) {
        val args = mapOf("token" to token, "userUID" to userUID, "pageUID" to pageUID)
        model.getPosts(args, callback(
                onResponse = {body: ArrayList<Post>, _ -> cFragment?.onPostsLoad(body) },
                onFailure = { cFragment?.onError() }
        ))
    }
}

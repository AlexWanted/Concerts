package ru.rewindforce.concerts.profile

import okhttp3.MultipartBody
import okhttp3.RequestBody
import ru.rewindforce.concerts.utils.Model

class ProfileModel: Model() {

    //private val TAG: String = ProfileModel::class.java.simpleName
    private val profileApi: Model.ProfileApi = retrofit.create(Model.ProfileApi::class.java)

    fun getFriendsList(args: Map<String, Any?>, callback: ResponseCallback) {
        val token: String by args
        val authUID: String by args
        val userUID: String by args
        profileApi.getFriendsList(token, authUID, userUID).enqueue(callback)
    }

    fun getIncomingRequests(args: Map<String, Any?>, callback: ResponseCallback) {
        val token: String by args
        val userUID: String by args
        profileApi.getIncomingRequests(token, userUID).enqueue(callback)
    }

    fun getOutgoingRequests(args: Map<String, Any?>, callback: ResponseCallback) {
        val token: String by args
        val userUID: String by args
        profileApi.getOutgoingRequests(token, userUID).enqueue(callback)
    }

    fun getRequestsCount(args: Map<String, Any?>, callback: ResponseCallback) {
        val token: String by args
        val userUID: String by args
        profileApi.getRequestsCount(token, userUID).enqueue(callback)
    }

    fun addFriend(args: Map<String, Any?>, callback: ResponseCallback) {
        val token: String by args
        val senderUID: String by args
        val receiverUID: String by args
        profileApi.addFriend(receiverUID, token, senderUID).enqueue(callback)
    }

    fun deleteFriend(args: Map<String, Any?>, callback: ResponseCallback) {
        val token: String by args
        val senderUID: String by args
        val receiverUID: String by args
        profileApi.deleteFriend(receiverUID, token, senderUID).enqueue(callback)
    }

    fun cancelRequest(args: Map<String, Any?>, callback: ResponseCallback) {
        val token: String by args
        val senderUID: String by args
        val receiverUID: String by args
        profileApi.cancelRequest(receiverUID, token, senderUID).enqueue(callback)
    }

    fun rejectRequest(args: Map<String, Any?>, callback: ResponseCallback) {
        val token: String by args
        val senderUID: String by args
        val receiverUID: String by args
        profileApi.rejectRequest(senderUID, token, receiverUID).enqueue(callback)
    }

    fun acceptRequest(args: Map<String, Any?>, callback: ResponseCallback) {
        val token: String by args
        val senderUID: String by args
        val receiverUID: String by args
        profileApi.acceptRequest(senderUID, token, receiverUID).enqueue(callback)
    }

    fun getProfileInfo(args: Map<String, Any?>, callback: ResponseCallback) {
        val token: String by args
        val userUID: String by args
        val login: String by args
        profileApi.getUserInfo(token, userUID, login).enqueue(callback)
    }

    fun getPosts(args: Map<String, Any?>, callback: ResponseCallback) {
        val token: String by args
        val userUID: String by args
        val pageUID: String by args
        profileApi.getPosts(pageUID, token, userUID).enqueue(callback)
    }

    fun editProfile(token: String?, uid: RequestBody?, firstName: RequestBody?,
                    lastName: RequestBody?, avatar: MultipartBody.Part?, header: MultipartBody.Part?,
                    callback: ResponseCallback) {
        profileApi.patchUser(token, uid, firstName, lastName, avatar, header).enqueue(callback)
    }
}

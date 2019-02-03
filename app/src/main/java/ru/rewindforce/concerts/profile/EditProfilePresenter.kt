package ru.rewindforce.concerts.profile

import okhttp3.MediaType
import okhttp3.RequestBody
import org.jetbrains.anko.support.v4.ctx
import ru.rewindforce.concerts.utils.PREF_TOKEN
import ru.rewindforce.concerts.utils.PREF_UID
import ru.rewindforce.concerts.utils.Presenter
import ru.rewindforce.concerts.utils.getStringPref

class EditProfilePresenter: Presenter<ProfileModel>() {
    init { this.model = ProfileModel() }

    fun editProfileInfo(firstName: String, lastName: String,
                        avatarByteArray: ByteArray?, headerByteArray: ByteArray?) {
        val partFirstName = RequestBody.create(MediaType.parse("text/plain"), firstName)
        val partLastName = RequestBody.create(MediaType.parse("text/plain"), lastName)

        fragment?.let {
            val avatar = createImagePart(it.ctx, "tempavatar.jpg", "avatar", avatarByteArray)
            val header = createImagePart(it.ctx, "tempheaer.jpg", "header", headerByteArray)
            val token = it.getStringPref(PREF_TOKEN) ?: ""
            val userUID = it.getStringPref(PREF_UID) ?: ""
            val partUID = RequestBody.create(MediaType.parse("text/plain"), userUID)

            model.editProfile(token, partUID, partFirstName, partLastName, avatar, header, callback(
                    onResponse = {_: Any, _ -> it.onSuccess()},
                    onFailure = { it.onError() }
            ))
        }
    }
}
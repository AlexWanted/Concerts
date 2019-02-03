package ru.rewindforce.concerts.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

private const val HOST: String = "https://rewindconcerts.000webhostapp.com"

@Parcelize
data class CommentAuthor(val authorLogin: String = "", val authorUID: String = "",
                         val authorAvatarUrl: String = "/photoalbums/defaultavatar.jpg"): Parcelable {
    val authorAvatar: String
        get() = if (authorAvatarUrl == "/photoalbums/defaultavatar.jpg") "$HOST$authorAvatarUrl"
                else "$HOST/photoalbums/$authorUID/avatars/$authorAvatarUrl"
}
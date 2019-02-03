package ru.rewindforce.concerts.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

private const val HOST: String = "https://rewindconcerts.000webhostapp.com"

@Parcelize
data class User(val firstName: String = "Роланд", val lastName: String = "Дискейн",
                val city: String = "Москва",
                val avatar: String = "/photoalbums/defaultavatar.jpg",
                val header: String = "/photoalbums/defaultheader.jpg",
                val UID: String = "", val login: String = "gunslinger",
                val concertsCount: Int = 0, val friendsCount: Int = 0,
                var isInFriends: Boolean = false, var isInRequests: Boolean = false,
                var isInIncomes: Boolean = false): Parcelable {
    val avatarURL: String
        get() = if (avatar == "/photoalbums/defaultavatar.jpg") HOST +avatar
                else "$HOST/photoalbums/$UID/avatars/$avatar"

    val headerURL: String
        get() = if (header == "/photoalbums/defaultheader.jpg") HOST +header
                else "$HOST/photoalbums/$UID/headers/$header"

    val fullName: String
        get() = "$firstName $lastName"
}
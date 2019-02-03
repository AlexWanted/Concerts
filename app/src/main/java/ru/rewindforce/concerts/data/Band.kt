package ru.rewindforce.concerts.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

private const val HOST: String = "https://rewindconcerts.000webhostapp.com"

@Parcelize
data class Band(val UID: String = "", val bandName: String = "", val bandCountry: String = "",
                private val bandAvatar: String = "",
                private val bandGenre: ArrayList<String> = ArrayList()): Parcelable {
    val avatarURL: String
        get() = "$HOST$bandAvatar"

    val genre: String
        get() = bandGenre[0]
}

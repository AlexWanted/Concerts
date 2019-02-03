package ru.rewindforce.concerts.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

private const val HOST: String = "https://rewindconcerts.000webhostapp.com"

@Parcelize
data class Concert(val UID: String = "", val title: String = "", val club: String = "",
                   val description: String = "", val lowresPoster: String = "",
                   val highresPoster: String = "", val city: String = "",
                   val lineUp: ArrayList<String> = ArrayList(),
                   val datetime: Long = 0,
                   var commentsCount: Int = 0) : Parcelable {
    val lowresPosterURL: String
        get() = if (lowresPoster == "/photoalbums/defaultheader.jpg") HOST +lowresPoster
                else "$HOST/concertposters/$UID/lowres/$lowresPoster"

    val highresPosterURL: String
        get() = if (highresPoster == "/photoalbums/defaultheader.jpg") HOST +highresPoster
                else "$HOST/concertposters/$UID/highres/$highresPoster"

    val bandsCount: Int
        get() = lineUp.size

    fun incrementCommentsCount() = commentsCount++
    fun decrementCommentsCount() = commentsCount--
}
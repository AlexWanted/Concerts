package ru.rewindforce.concerts.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.ArrayList

@Parcelize
data class Comment(val commentUID: String = "", var commentMessage: String = "",
                   val commentDatetime: Long = 0,
                   var haveUserLikedThis: Boolean = false,
                   val commentLikes: ArrayList<String> = ArrayList(),
                   val commentAuthor: CommentAuthor = CommentAuthor()): Parcelable {
    var userLike: Boolean
        get() = haveUserLikedThis
        set(value) { haveUserLikedThis = value }

    val commentLikesCount: Int
        get() = commentLikes.size

    val authorLogin: String
        get() = commentAuthor.authorLogin

    val authorUID: String
        get() = commentAuthor.authorUID

    val authorAvatar: String
        get() = commentAuthor.authorAvatar
}

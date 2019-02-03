package ru.rewindforce.concerts.data

private const val HOST: String = "https://rewindconcerts.000webhostapp.com"

data class Post(val postUID: String = "", val postText: String = "", val postDatetime: Long = 0,
                val postAuthor: User = User(), private val postAttachments: ArrayList<String> = ArrayList(),
                val postLikes: MutableList<String> = mutableListOf(), val postReposts: List<String> = listOf(),
                val postComments: List<String> = listOf()) {

    var likesCount: Int = 0
        get() = postLikes.size
    val repostsCount: Int
        get() = postReposts.size
    val commentsCount: Int
        get() = postComments.size

    fun getAttachments(): ArrayList<String> {
        val tempList: ArrayList<String> = ArrayList()
        postAttachments.forEach{tempList.add("$HOST/photoalbums/${postAuthor.UID}/wallimages/$it")}
        return tempList
    }
}
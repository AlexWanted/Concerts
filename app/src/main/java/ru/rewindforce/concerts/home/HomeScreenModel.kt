package ru.rewindforce.concerts.home

import ru.rewindforce.concerts.utils.Model

class HomeScreenModel: Model() {
    private val concertsApi: Model.ConcertsApi = retrofit.create(Model.ConcertsApi::class.java)

    fun getFlatConcertsList(args: Map<String, Any?>, callback: ResponseCallback) {
        val limit: Int by args
        val offset: Int by args
        val orderBy: String by args
        val ascDesc: String by args
        concertsApi.getData(limit, offset, orderBy, ascDesc).enqueue(callback)
    }

    fun getWishlist(args: Map<String, Any?>, callback: ResponseCallback) {
        val limit: Int by args
        val offset: Int by args
        val ascDesc: String by args
        val viewerToken: String by args
        val viewerUID: String by args
        val userUID: String by args
        concertsApi.getWishlist(viewerToken, viewerUID, userUID, limit, offset, ascDesc).enqueue(callback)
    }
}
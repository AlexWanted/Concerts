package ru.rewindforce.concerts.home

import ru.rewindforce.concerts.data.Concert
import ru.rewindforce.concerts.utils.BaseFragment
import ru.rewindforce.concerts.utils.Presenter

class HomeScreenPresenter(private val cFragment: BaseFragment?): Presenter<HomeScreenModel>(cFragment) {
    init { this.model = HomeScreenModel() }

    fun loadUpcomingConcerts(token: String, viewerUID: String, userUID: String, limit: Int, offset: Int) {
        val args = mapOf("viewerToken" to token, "viewerUID" to viewerUID, "userUID" to userUID,
                "limit" to limit, "offset" to offset, "ascDesc" to "ASC")
        model.getWishlist(args, callback(
                onResponse = { body: ArrayList<Concert>, _ -> cFragment?.onConcertsLoad(body)},
                onFailure = { cFragment?.onError() }
        ))
    }

    fun loadPastConcerts(token: String, viewerUID: String, userUID: String, limit: Int, offset: Int) {
        val args = mapOf("viewerToken" to token, "userUID" to userUID,"viewerUID" to viewerUID,
                "limit" to limit, "offset" to offset, "ascDesc" to "DESC")
        model.getWishlist(args, callback(
                onResponse = { body: ArrayList<Concert>, _ -> cFragment?.onConcertsLoad(body)},
                onFailure = { cFragment?.onError() }
        ))
    }

    fun loadConcerts(limit: Int, offset: Int, orderBy: String, ascDesc: String) {
        val args = mapOf("limit" to limit, "offset" to offset, "ascDesc" to ascDesc, "orderBy" to orderBy)
        model.getFlatConcertsList(args, callback(
                onResponse = { body: ArrayList<Concert>, _ -> cFragment?.onConcertsLoad(body)},
                onFailure = { cFragment?.onError() }
        ))
    }
}
package ru.rewindforce.concerts.home

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.rewindforce.concerts.R
import kotlinx.android.synthetic.main.fragment_concerts_overview.*
import ru.rewindforce.concerts.HomepageActivity
import ru.rewindforce.concerts.HomepageActivity.Companion.CONCERT_DETAILS
import ru.rewindforce.concerts.data.Concert
import ru.rewindforce.concerts.details.ConcertDetailsFragment
import ru.rewindforce.concerts.utils.*
import java.util.ArrayList

private const val BUNDLE_LIST_STATE = "bundle_list_state"
private const val BUNDLE_OFFSET = "bundle_offset"
private const val BUNDLE_LIST = "bundle_concerts_list"
private const val BUNDLE_FRAGMENT_STATE = "bundle_fragment_state"
private const val BUNDLE_CURRENT_UID = "bundle_current_uid"

class ConcertsListFragment: BaseFragment(R.layout.fragment_concerts_overview), HomepageActivity.OnTabReselected {

    private val TAG: String = ConcertsListFragment::class.java.simpleName

    private var currentState: Int = 0
    private val presenter: HomeScreenPresenter by lazy { HomeScreenPresenter(this) }
    private lateinit var concertsAdapter: ConcertAdapter
    private var concertsLayoutManager: GridLayoutManager? = null
    private var offset = 0
    private val count = 8
    private var pastVisibleItems = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private var currentUID = ""
    private var canLoad = true
    private var listState: Parcelable? = null
    private val concertsList: ArrayList<Concert?> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentState = it.getInt(BUNDLE_FRAGMENT_STATE)
            currentUID = it.getString(BUNDLE_CURRENT_UID) ?: ""
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshLayout.isEnabled = false

        concertsLayoutManager = GridLayoutManager(context, if (currentState == ARG_OVERVIEW) 2 else 1)
        when(currentState) {
            ARG_OVERVIEW -> {
                concertsList.add(null)
                concertsLayoutManager?.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int = if (position == 0) 2 else 1
                }
                enableWhiteStatus = false
            }
            ARG_PAST, ARG_UPCOMING -> enableWhiteStatus = true
        }
        concertsAdapter = ConcertAdapter(concertsList, currentState)
        concertsAdapter.setOnConcertClickedListener(object: ConcertAdapter.OnConcertClicked {
            override fun onConcertClicked(concert: Concert) {
                openChildFragment(CONCERT_DETAILS, ConcertDetailsFragment.newInstance(concert), childContainer.id)
            }
        })
        concertsRecycler.layoutManager = concertsLayoutManager
        concertsRecycler.adapter = concertsAdapter

        savedInstanceState.let {
            if (it != null) {
                val parcelList: ArrayList<Concert?> = it.getParcelableArrayList(BUNDLE_LIST) ?: ArrayList()
                concertsList.clear()
                concertsList.addAll(parcelList)
                offset = it.getInt(BUNDLE_OFFSET)
                listState = it.getParcelable(BUNDLE_LIST_STATE)
                concertsLayoutManager?.onRestoreInstanceState(listState)
            } else if (concertsList.contains(null) && concertsList.size == 1 || concertsList.size == 0) {
                startLoadingConcerts()
            } else {}
        }

        concertsRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItemCount = concertsLayoutManager?.childCount ?: 0
                    totalItemCount = concertsLayoutManager?.itemCount ?: 0
                    pastVisibleItems = concertsLayoutManager?.findFirstVisibleItemPosition() ?: 0

                    if (canLoad) {
                        if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                            canLoad = false
                            offset += count
                            startLoadingConcerts()
                        }
                    }
                }
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter.attach(this)
    }

    override fun onDetach() {
        super.onDetach()
        presenter.detach()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        listState = concertsLayoutManager?.onSaveInstanceState()
        outState.putParcelable(BUNDLE_LIST_STATE, listState)
        outState.putInt(BUNDLE_OFFSET, offset)
        outState.putSerializable(BUNDLE_LIST, concertsList)
    }

    internal fun startLoadingConcerts() {
        refreshLayout.isRefreshing = true
        val token = getStringPref(PREF_TOKEN) ?: ""
        val viewerUID = getStringPref(PREF_UID) ?: ""
        when (currentState) {
            ARG_PAST -> presenter.loadPastConcerts(token, viewerUID, currentUID, count, offset)
            ARG_OVERVIEW -> presenter.loadConcerts(count, offset, "datetime", "ASC")
            ARG_UPCOMING -> presenter.loadUpcomingConcerts(token, viewerUID, currentUID, count, offset)
        }
    }

    override fun onConcertsLoad(concerts: ArrayList<Concert>) {
        concertsAdapter.notifyItemChanged(0)

        concerts.forEachIndexed { index, concert ->
            val newIndex = offset + (if (currentState == ARG_OVERVIEW) 1 else 0) + (index+1)
            concertsList.add(concert)
            concertsAdapter.notifyItemInserted(newIndex)
        }
        canLoad = true
        refreshLayout?.isRefreshing = false
    }

    override fun onError() {
        canLoad = true
        offset -= count
        refreshLayout?.isRefreshing = false
    }

    override fun onReselected() {
        if (concertsRecycler != null) concertsRecycler.smoothScrollToPosition(0)
    }

    companion object {
        const val ARG_PAST = 0
        const val ARG_OVERVIEW = 1
        const val ARG_UPCOMING = 2

        @JvmStatic
        fun newInstance(fragmentType: Int, currentUID: String) = ConcertsListFragment().apply {
            arguments = Bundle().apply {
                putInt(BUNDLE_FRAGMENT_STATE, fragmentType)
                putString(BUNDLE_CURRENT_UID, currentUID)
            }
        }
    }
}
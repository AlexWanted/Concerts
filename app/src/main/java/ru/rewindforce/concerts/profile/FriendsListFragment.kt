package ru.rewindforce.concerts.profile

import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_recycler.*
import kotlinx.android.synthetic.main.item_header_appbar.*
import org.jetbrains.anko.support.v4.ctx
import ru.rewindforce.concerts.HomepageActivity
import ru.rewindforce.concerts.utils.ListAdapter.OnItemClicked
import ru.rewindforce.concerts.utils.ListAdapter.OnRequestsClicked
import ru.rewindforce.concerts.R
import ru.rewindforce.concerts.data.User
import ru.rewindforce.concerts.utils.*

private const val BUNDLE_UID = "bundle_uid"

class FriendsListFragment: BaseFragment(R.layout.fragment_recycler, true) {

    private val TAG: String = FriendsListFragment::class.java.simpleName

    private var userUID: String? = null

    private val presenter: ProfilePresenter by lazy { ProfilePresenter(this) }
    private lateinit var adapter: ListAdapter
    private val itemsList = arrayListOf<Any>()
    private val friendsList: ArrayList<User> = ArrayList()

    private var requestsCount = 0
    private var haveRequestsCountLoaded = false
    private var haveFriendsLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { userUID = it.getString(BUNDLE_UID) }
        itemsList.add("Друзья")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        refreshLayout.isEnabled = false

        adapter = ListAdapter(itemsList, 2)

        adapter.setOnItemClickListener(object: OnItemClicked {
            override fun onItemClicked(login: String, uid: String) {
                                  openChildFragment(HomepageActivity.PROFILE_FRAGMENT,
                                  ProfileFragment.newInstance(uid, login),
                                  childContainer.id) }
        })
        adapter.setOnRequestsClickListener(object: OnRequestsClicked {
             override fun onRequestsClicked() {
                 val fragmentsList: ArrayList<ParcelFragment> = ArrayList()
                 val tabsList: ArrayList<String> = ArrayList()
                 fragmentsList.add(RequestsListFragment.newInstance(0))
                 tabsList.add("Входящие")
                 fragmentsList.add(RequestsListFragment.newInstance(1))
                 tabsList.add("Исходящие")
                openChildFragment(HomepageActivity.REQUESTS_LIST_FRAGMENT,
                        TabsFragment.newInstance(fragmentsList, tabsList), childContainer.id) }
        })

        val linearLayoutManager = LinearLayoutManager(ctx, RecyclerView.VERTICAL, false)
        concertsRecycler.layoutManager = linearLayoutManager
        concertsRecycler.adapter = adapter
        if (itemsList.size == 1) {
            if (getStringPref(PREF_UID) == userUID) presenter.getRequestsCount(getStringPref(PREF_TOKEN), userUID)
            else haveRequestsCountLoaded = true
            presenter.getFriendsList(getStringPref(PREF_TOKEN), getStringPref(PREF_UID), userUID)
            refreshLayout.isRefreshing = true
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter.attach(this)
    }

    override fun onDetach() {
        super.onDetach()
        presenter.detach()
    }

    override fun onError() { refreshLayout.isRefreshing = false }

    override fun onUsersLoad(users: ArrayList<User>) {
        haveFriendsLoaded = true
        users.forEachIndexed{ _, user -> run {
            friendsList.add(user)
        }}
        checkIfEverythingLoaded()
    }

    override fun onRequestsCount(count: Int) {
        haveRequestsCountLoaded = true
        requestsCount = count
        checkIfEverythingLoaded()
    }

    private fun checkIfEverythingLoaded() { if (haveRequestsCountLoaded && haveFriendsLoaded) populateAll() }

    private fun populateAll() {
        refreshLayout.isRefreshing = false
        returnButton.setOnClickListener { activity?.onBackPressed() }
        if (!itemsList.contains(requestsCount)) {
            itemsList.add(requestsCount)
            adapter.notifyItemInserted(itemsList.indexOf(requestsCount))
        }
        friendsList.forEachIndexed{pos, user -> run {
            itemsList.add(user)
            adapter.notifyItemInserted(pos)
        }}
    }

    companion object {
        @JvmStatic
        fun newInstance(UID: String?) = FriendsListFragment().apply {
            arguments = Bundle().apply { putString(BUNDLE_UID, UID) }
        }
    }
}
package ru.rewindforce.concerts.profile

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_recycler.*
import org.jetbrains.anko.support.v4.ctx
import ru.rewindforce.concerts.HomepageActivity
import ru.rewindforce.concerts.R
import ru.rewindforce.concerts.data.User
import ru.rewindforce.concerts.utils.*

const val STATE_INCOMING = 0
const val STATE_OUTGOING = 1

private const val BUNDLE_STATE = "bundle_state"

class RequestsListFragment: BaseFragment(R.layout.fragment_recycler, true) {

    private val TAG: String = RequestsListFragment::class.java.simpleName

    private var currentState: Int = 0
    private val requestsList: ArrayList<User> = ArrayList()
    private lateinit var adapter: ListAdapter
    private val presenter: ProfilePresenter by lazy { ProfilePresenter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { currentState = it.getInt(BUNDLE_STATE) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshLayout.isEnabled = false

        adapter = ListAdapter(requestsList, if (currentState == STATE_INCOMING) 5 else 6)
        adapter.setOnItemClickListener(object : ListAdapter.OnItemClicked {
            override fun onItemClicked(login: String, uid: String) {
                            openChildFragment(HomepageActivity.PROFILE_FRAGMENT,
                                    ProfileFragment.newInstance(uid, login),
                                    childContainer.id)
            }
        })
        val linearLayoutManager = LinearLayoutManager(ctx, RecyclerView.VERTICAL, false)
        concertsRecycler.layoutManager = linearLayoutManager
        concertsRecycler.adapter = adapter

        when (currentState) {
            STATE_INCOMING -> {
                if (requestsList.size == 0) {
                    refreshLayout.isRefreshing = true
                    presenter.getIncomingRequests(getStringPref(PREF_TOKEN), getStringPref(PREF_UID))
                }
                adapter.setOnActionsClickListener(object: ListAdapter.OnActionsClicked() {
                    override fun onAcceptClicked(pos: Int, UID: String) { attemptToAcceptRequest(UID, pos) }
                    override fun onRejectClicked(pos: Int, UID: String) { attemptToRejectRequest(UID, pos) }
                })
            }
            STATE_OUTGOING -> {
                if (requestsList.size == 0) {
                    refreshLayout.isRefreshing = true
                    presenter.getOutgoingRequests(getStringPref(PREF_TOKEN), getStringPref(PREF_UID))
                }
                adapter.setOnActionsClickListener(object: ListAdapter.OnActionsClicked() {
                    override fun onCancelClicked(pos: Int, UID: String) { attemptToCancelRequest(UID, pos) }
                })
            }
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

    private fun attemptToRejectRequest(senderUID: String, pos: Int) {
        val userUID = getStringPref(PREF_UID)
        if (userUID != senderUID) {
            val builder = AlertDialog.Builder(ctx).apply {
                setView(View.inflate(ctx, R.layout.dialog_confirmantion, null))
                setTitle("Подтверждение")
                setMessage("Вы точно уверены, что хотите отклонить эту заявку в друзья?")
                setPositiveButton("Да") { _, _ -> presenter.rejectRequest(getStringPref(PREF_TOKEN), userUID, senderUID, pos) }
                setNegativeButton("Нет") { dialog: DialogInterface, _ -> dialog.dismiss() }
            }
            builder.show()
        }
    }

    override fun onFriendDelete(pos: Int) {
        requestsList.removeAt(pos)
        adapter.notifyItemRemoved(pos)
    }

    private fun attemptToAcceptRequest(senderUID: String, pos: Int) {
        val userUID = getStringPref(PREF_UID)
        if (userUID != senderUID) presenter.acceptRequest(getStringPref(PREF_TOKEN), userUID, senderUID, pos)
    }

    override fun onFriendAdded(pos: Int) {
        requestsList.removeAt(pos)
        adapter.notifyItemRemoved(pos)
    }

    private fun attemptToCancelRequest(senderUID: String, pos: Int) {
        val userUID = getStringPref(PREF_UID)
        if (userUID != senderUID) {
            val builder = AlertDialog.Builder(ctx).apply {
                setView(View.inflate(ctx, R.layout.dialog_confirmantion, null))
                setTitle("Подтверждение")
                setMessage("Вы точно уверены, что хотите отменить эту заявку в друзья?")
                setPositiveButton("Да") { _, _ -> presenter.cancelRequest(getStringPref(PREF_TOKEN), userUID, senderUID, pos) }
                setNegativeButton("Нет") { dialog: DialogInterface, _ -> dialog.dismiss() }
            }
            builder.show()
        }
    }

    override fun onUsersLoad(users: ArrayList<User>) {
        refreshLayout.isRefreshing = false
        users.forEachIndexed{ pos, user -> run {
            requestsList.add(user)
            adapter.notifyItemInserted(pos)
        }}
    }

    override fun onError() { refreshLayout.isRefreshing = false }

    companion object {
        @JvmStatic
        fun newInstance(state: Int) = RequestsListFragment().apply {
            arguments = Bundle().apply { putInt(BUNDLE_STATE, state) }
        }
    }
}
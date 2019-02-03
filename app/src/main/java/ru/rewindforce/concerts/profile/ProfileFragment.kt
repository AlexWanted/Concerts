package ru.rewindforce.concerts.profile

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AlertDialog

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import kotlinx.android.synthetic.main.fragment_profile.*
import org.jetbrains.anko.support.v4.ctx
import ru.rewindforce.concerts.home.ConcertsListFragment
import ru.rewindforce.concerts.HomepageActivity.Companion.EDIT_PROFILE_FRAGMENT
import ru.rewindforce.concerts.HomepageActivity.Companion.FRIENDS_LIST_FRAGMENT
import ru.rewindforce.concerts.HomepageActivity.Companion.MY_CONCERTS_FRAGMENT
import ru.rewindforce.concerts.R
import ru.rewindforce.concerts.adapters.PostsAdapter
import ru.rewindforce.concerts.data.Post
import ru.rewindforce.concerts.data.User
import ru.rewindforce.concerts.utils.*
import java.util.ArrayList


private const val BUNDLE_UID = "bundle_uid"
private const val BUNDLE_LOGIN = "bundle_login"
private const val BUNDLE_USER = "bundle_user"

class ProfileFragment : BaseFragment(R.layout.fragment_profile) {

    private val TAG: String = ProfileFragment::class.java.simpleName

    private var currentUser: User? = null
    private var login: String? = null
    private var uid: String? = null

    private var firstName: String? = null
    private var lastName: String? = null
    private var avatarURL: String? = null
    private var headerURL: String? = null

    private val presenter: ProfilePresenter by lazy { ProfilePresenter(this) }
    private val postsList: ArrayList<Post> = ArrayList()
    private val postsAdapter: PostsAdapter by lazy { PostsAdapter(postsList, uid ?: "") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            uid = it.getString(BUNDLE_UID)
            login = it.getString(BUNDLE_LOGIN)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshLayout.isEnabled = false
        addFriendButton.visibility = if(getStringPref(PREF_LOGIN) == login) GONE else VISIBLE
        editProfileButton.visibility = if(getStringPref(PREF_LOGIN) == login) VISIBLE else GONE
        friendsBlock.setOnClickListener { openChildFragment(FRIENDS_LIST_FRAGMENT,
                                          FriendsListFragment.newInstance(uid),
                                          childContainer.id) }
        concertsBlock.setOnClickListener {
            val fragmentsList: ArrayList<ParcelFragment> = ArrayList()
            val tabsList: ArrayList<String> = ArrayList()
            fragmentsList.add(ConcertsListFragment.newInstance(ConcertsListFragment.ARG_PAST, currentUser?.UID ?: ""))
            tabsList.add("Прошедшие")
            openChildFragment(MY_CONCERTS_FRAGMENT, TabsFragment.newInstance(fragmentsList, tabsList), childContainer.id)
        }
        returnButton.setOnClickListener { activity?.onBackPressed() }
        editProfileButton.setOnClickListener { openChildFragment(EDIT_PROFILE_FRAGMENT,
                EditProfileFragment.newInstance(firstName, lastName, avatarURL, headerURL),
                childContainer.id)}

        val layoutManager = LinearLayoutManager(ctx, RecyclerView.VERTICAL, false)
        postsAdapter.setOnLikeClickListener(object: PostsAdapter.LikeClickListener {
            override fun onDislike(position: Int) {
                postsList[position].postLikes.remove(uid ?: "")
            }

            override fun onLike(position: Int) {
                postsList[position].postLikes.add(uid ?: "")
            }
        })
        postsRecycler.adapter = postsAdapter
        postsRecycler.layoutManager = layoutManager
        /*val medias: ArrayList<String> = ArrayList()
        medias.add("https://pp.userapi.com/c840224/v840224217/18c4e/7-x5Py4NuL0.jpg")
        medias.add("https://pp.userapi.com/c840224/v840224217/18c60/eLKCoVGZ4pI.jpg")
        medias.add("https://pp.userapi.com/c840224/v840224217/18c57/Lv10NmzrrBM.jpg")
        medias.add("https://pp.userapi.com/c840224/v840224217/18c18/rvckH-RKc-c.jpg")
        medias.add("https://pp.userapi.com/c840224/v840224217/18c45/RmZvhfSZL_E.jpg")
        medias.add("https://pp.userapi.com/c840224/v840224217/18c3c/WZ4u94qUXaQ.jpg")
        medias.add("https://pp.userapi.com/c840224/v840224217/18c33/3E3uMnBJRJY.jpg")
        medias.add("https://pp.userapi.com/c840224/v840224217/18c2a/tvBRb_r9u8w.jpg")
        medias.add("https://pp.userapi.com/c840224/v840224217/18c21/VCKgA59MVD0.jpg")
        medias.add("https://pp.userapi.com/c840224/v840224217/18c18/rvckH-RKc-c.jpg")
        mediaView.setImages(medias)*/
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null) currentUser = savedInstanceState.getParcelable(BUNDLE_USER)

        if (currentUser == null) {
            profilePage.visibility = GONE
            presenter.getProfileInfo(getStringPref(PREF_TOKEN), getStringPref(PREF_UID), login)
            presenter.getPosts(getStringPref(PREF_TOKEN) ?: "", getStringPref(PREF_UID) ?: "", uid ?: "")
            refreshLayout.isRefreshing = true
        } else {
            onProfileInfo(currentUser)
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (currentUser != null) outState.putParcelable(BUNDLE_USER, currentUser)
    }

    override fun onProfileInfo(user: User?) {
        refreshLayout.isRefreshing = false
        profilePage.visibility = VISIBLE
        currentUser = user

        firstName = user?.firstName
        lastName = user?.lastName
        avatarURL = user?.avatarURL
        headerURL = user?.headerURL

        Glide.with(ctx).load(avatarURL).thumbnail(0.1f).apply(RequestOptions().circleCrop()).into(avatar)
        Glide.with(ctx).load(headerURL).thumbnail(0.1f).into(header)

        fullName.text = user?.fullName

        loginText.text = login

        visitedConcertsNumber.text = user?.concertsCount.toString()
        visitedConcertsText.text = ctx.resources.getQuantityString(R.plurals.concerts_plural,
                                                                   user?.concertsCount ?: 0)
        friendsCountNumber.text = user?.friendsCount.toString()

        friendsCountText.text = ctx.resources.getQuantityString(R.plurals.friends_plural,
                                                                user?.friendsCount ?: 0)

        when {
            user?.isInFriends ?: false -> onFriendAdded()
            user?.isInRequests ?: false -> onRequestSent()
            user?.isInIncomes ?: false -> isInIncomes()
            else -> onFriendDelete()
        }
    }

    override fun onError() { refreshLayout.isRefreshing = false }

    private fun attemptToAddFriend() {
        val senderUID = getStringPref(PREF_UID)
        if (senderUID != uid) presenter.addFriend(getStringPref(PREF_TOKEN), senderUID, uid)
    }

    private fun attemptToAcceptRequest() {
        val senderUID = getStringPref(PREF_UID)
        if (senderUID != uid) presenter.acceptRequest(getStringPref(PREF_TOKEN), senderUID, uid)
    }

    override fun onRequestSent() {
        currentUser?.isInRequests = true
        currentUser?.isInFriends = false
        currentUser?.isInIncomes = false
        addFriendButton.text = "Отменить заявку"
        addFriendButton.backgroundTintList = ContextCompat.getColorStateList(ctx, R.color.error)
        addFriendButton.icon = ContextCompat.getDrawable(ctx, R.drawable.ic_friend_remove)
        rejectRequestButton.visibility = View.GONE
        addFriendButton.setOnClickListener { attemptToCancelRequest() }
    }

    private fun isInIncomes() {
        addFriendButton.text = "Принять"
        addFriendButton.backgroundTintList = ContextCompat.getColorStateList(ctx, R.color.colorAccent)
        addFriendButton.icon = ContextCompat.getDrawable(ctx, R.drawable.ic_friend_add)
        rejectRequestButton.visibility = View.VISIBLE
        rejectRequestButton.backgroundTintList = ContextCompat.getColorStateList(ctx, R.color.error)
        rejectRequestButton.setOnClickListener { attemptToRejectRequest() }
        addFriendButton.setOnClickListener { attemptToAcceptRequest() }
    }

    override fun onFriendAdded(pos: Int) {
        currentUser?.isInRequests = false
        currentUser?.isInIncomes = false
        currentUser?.isInFriends = true
        addFriendButton.text = "Удалить из друзей"
        addFriendButton.backgroundTintList = ContextCompat.getColorStateList(ctx, R.color.error)
        addFriendButton.icon = ContextCompat.getDrawable(ctx, R.drawable.ic_friend_remove)
        rejectRequestButton.visibility = View.GONE
        addFriendButton.setOnClickListener { attemptToDeleteFriend() }
    }

    private fun attemptToDeleteFriend() {
        val senderUID = getStringPref(PREF_UID)
        if (senderUID != uid) {
            val builder = AlertDialog.Builder(ctx).apply {
                setView(View.inflate(ctx, R.layout.dialog_confirmantion, null))
                setTitle("Подтверждение")
                setMessage("Вы точно уверены, что хотите удалить $login из друзей?")
                setPositiveButton("Удалить") { _, _ -> presenter.deleteFriend(getStringPref(PREF_TOKEN), senderUID, uid) }
                setNegativeButton("Отмена") { dialog: DialogInterface, _ -> dialog.dismiss() }
            }
            builder.show()
        }
    }

    private fun attemptToRejectRequest() {
        val senderUID = getStringPref(PREF_UID)
        if (senderUID != uid) {
            val builder = AlertDialog.Builder(ctx).apply {
                setView(View.inflate(ctx, R.layout.dialog_confirmantion, null))
                setTitle("Подтверждение")
                setMessage("Вы точно уверены, что хотите отклонить эту заявку в друзья?")
                setPositiveButton("Да") { _, _ -> presenter.rejectRequest(getStringPref(PREF_TOKEN), senderUID, uid) }
                setNegativeButton("Нет") { dialog: DialogInterface, _ -> dialog.dismiss() }
            }
            builder.show()
        }
    }

    override fun onFriendDelete(pos: Int) {
        currentUser?.isInFriends = false
        currentUser?.isInRequests = false
        currentUser?.isInIncomes = false
        addFriendButton.text = "Добавить в друзья"
        addFriendButton.backgroundTintList = ContextCompat.getColorStateList(ctx, R.color.colorAccent)
        addFriendButton.icon = ContextCompat.getDrawable(ctx, R.drawable.ic_friend_add)
        rejectRequestButton.visibility = View.GONE
        addFriendButton.setOnClickListener { attemptToAddFriend() }
    }

    private fun attemptToCancelRequest() {
        val senderUID = getStringPref(PREF_UID)
        if (senderUID != uid) {
            val builder = AlertDialog.Builder(ctx).apply {
                setView(View.inflate(ctx, R.layout.dialog_confirmantion, null))
                setTitle("Подтверждение")
                setMessage("Вы точно уверены, что хотите отменить эту заявку в друзья?")
                setPositiveButton("Да") { _, _ -> presenter.cancelRequest(getStringPref(PREF_TOKEN), senderUID, uid) }
                setNegativeButton("Нет") { dialog: DialogInterface, _ -> dialog.dismiss() }
            }
            builder.show()
        }
    }

    override fun onPostsLoad(posts: ArrayList<Post>) {
        posts.forEachIndexed { index, post ->
            postsList.add(post)
            postsAdapter.notifyItemInserted(index)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(UID: String, login: String) =
                ProfileFragment().apply {
                    arguments = Bundle().apply {
                        putString(BUNDLE_UID, UID)
                        putString(BUNDLE_LOGIN, login)
                    }
                }
    }
}

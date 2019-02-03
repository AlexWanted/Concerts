package ru.rewindforce.concerts.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.rewindforce.concerts.HomepageActivity
import ru.rewindforce.concerts.data.*
import java.util.ArrayList

abstract class BaseFragment(val layout: Int, var enableWhiteStatus: Boolean = false): ParcelFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(layout, container, false)

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (enableWhiteStatus) {
            when (hidden) {
                true -> (activity as HomepageActivity).setStatusBarWhite(false)
                false -> (activity as HomepageActivity).setStatusBarWhite(true)
            }
        } else {
            (activity as HomepageActivity).setStatusBarWhite(false)
        }
    }

    override fun onResume() {
        super.onResume()
        if (enableWhiteStatus) (activity as HomepageActivity).setStatusBarWhite(true)
        else (activity as HomepageActivity).setStatusBarWhite(false)
    }

    override fun onPause() {
        super.onPause()
        (activity as HomepageActivity).setStatusBarWhite(false)
    }

    open fun onStatusLoad(status: String) {}
    open fun onLineUpLoad(bands: ArrayList<Band>) {}
    open fun onConcertsLoad(buttonID: Int) {}
    open fun onCommentsLoad(comments: ArrayList<Comment>) {}
    open fun onCommentPosted(comment: Comment) {}
    open fun onCommentError() {}
    open fun onLiked(position: Int) {}
    open fun onDisliked(position: Int) {}
    open fun onCommentDeleted(position: Int) {}
    open fun onCommentEdited(message: String, position: Int) {}
    open fun onError() {}
    open fun onSuccess() {}

    open fun onUsersLoad(users: ArrayList<User>) {}
    open fun onProfileInfo(user: User?) {}
    open fun onRequestsCount(count: Int) {}
    open fun onFriendDelete(pos: Int = -1) {}
    open fun onRequestSent() {}
    open fun onFriendAdded(pos: Int = -1) {}

    open fun onConcertsLoad(concerts: ArrayList<Concert>) {}

    open fun onPostsLoad(posts: ArrayList<Post>) {}
}
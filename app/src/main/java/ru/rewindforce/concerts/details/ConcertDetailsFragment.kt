package ru.rewindforce.concerts.details

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.concert_details_comments_card.*
import kotlinx.android.synthetic.main.concert_details_description_card.*
import kotlinx.android.synthetic.main.concert_details_information_card.*
import kotlinx.android.synthetic.main.concert_details_line_up_card.*
import kotlinx.android.synthetic.main.fragment_concert_details.*
import org.jetbrains.anko.support.v4.ctx
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import ru.rewindforce.concerts.*
import ru.rewindforce.concerts.HomepageActivity.Companion.PROFILE_FRAGMENT
import ru.rewindforce.concerts.adapters.CommentsAdapter
import ru.rewindforce.concerts.authorization.AuthorizationActivity.PREF_ROLE
import ru.rewindforce.concerts.data.Band
import ru.rewindforce.concerts.data.Comment
import ru.rewindforce.concerts.data.Concert
import ru.rewindforce.concerts.profile.ProfileFragment
import ru.rewindforce.concerts.utils.*
import java.util.ArrayList

const val BUNDLE_CONCERT: String = "bundle_concert"

class ConcertDetailsFragment: BaseFragment(R.layout.fragment_concert_details) {

    private lateinit var concert: Concert
    private val presenter: ConcertDetailsPresenter by lazy { ConcertDetailsPresenter() }

    private val bandsList: ArrayList<Band?> = ArrayList()
    private val commentsList: ArrayList<Comment?> = ArrayList()

    private lateinit var lineUpAdapter: ListAdapter
    private lateinit var commentsAdapter: CommentsAdapter

    private var commentEditBoxDefTranslationY: Float = 0.0f
    private var isEditingComment: Boolean = false
    private var editedCommentUID: String = ""
    private var editedCommentPosition: Int = -1

    private var contentLoaded: Boolean = false
    private var canLoad: Boolean = true
    private var offset: Int = 0
    private val limit: Int = 8

    private val userToken: String by lazy { getStringPref(PREF_TOKEN) ?: "" }
    private val userUID: String by lazy { getStringPref(PREF_UID) ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { concert = it.getParcelable(BUNDLE_CONCERT) ?: Concert() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        descriptionCard.visibility = if (concert.description == "") View.GONE else View.VISIBLE
        descriptionText.text = concert.description

        commentEditBoxDefTranslationY = commentEditBox.translationY
        commentEditClose.setOnClickListener { clearEditedComment() }

        buttonSend.setOnClickListener { attemptToPostComment() }
        returnButton.setOnClickListener { activity?.onBackPressed() }

        toolbarTitle.text = concert.title
        infoClubName.text = concert.club
        infoCity.text = concert.city

        lineUpAdapter = ListAdapter(bandsList, 1)
        val lineUpLayoutManager = LinearLayoutManager(ctx, RecyclerView.VERTICAL, false)
        lineUpRecycler.layoutManager = lineUpLayoutManager
        lineUpRecycler.adapter = lineUpAdapter

        val onCommentActionsListener = object: CommentsAdapter.CommentActionsInterface {
            override fun onDeleteComment(commentUID: String, position: Int) {
                val builder = AlertDialog.Builder(ctx).apply {
                    setView(View.inflate(ctx, R.layout.dialog_confirmantion, null))
                    setTitle("Подтверждение")
                    setMessage("Вы точно уверены, что хотите удалить этот комментарий?")
                    setPositiveButton("Удалить") { _, _ -> attemptToDeleteComment(commentUID, position) }
                    setNegativeButton("Отмена") { dialog: DialogInterface, _ -> dialog.dismiss() }
                }
                builder.show()
            }

            override fun onEditComment(commentUID: String, position: Int) = setEditedComment(commentUID, position)

            override fun onLikeComment(commentUID: String, position: Int) =
                    presenter.likeComment(concert.UID, commentUID, userToken, userUID, position)

            override fun onDislikeComment(commentUID: String, position: Int) =
                    presenter.dislikeComment(concert.UID, commentUID, userToken, userUID, position)
        }

        commentsAdapter = CommentsAdapter(commentsList, userUID)
        commentsAdapter.setOnActionsListener(onCommentActionsListener)
        commentsAdapter.setOnCommentClickedListener (object: CommentsAdapter.OnCommentClicked {
            override fun onCommentClicked(UID: String, login: String, uid: String, position: Int) {
                val actionsDialog = CommentActionsDialogFragment.newInstance(
                        UID, uid, login, userUID, getStringPref(PREF_LOGIN),
                        getStringPref(PREF_ROLE), commentsList[position]?.userLike ?: false, position)
                actionsDialog.setOnActionsListener(onCommentActionsListener)
                actionsDialog.show(childFragmentManager, "comment_actions_dialog_fragment")
            }
        })

        commentsAdapter.setOnProfileClickedListener(object: CommentsAdapter.OnProfileClicked {
            override fun onProfileClicked(login: String, uid: String) {
                openChildFragment(PROFILE_FRAGMENT, ProfileFragment.newInstance(uid, login), childContainer.id)
            }
        })

        val commentsLayoutManager = LinearLayoutManager(ctx, RecyclerView.VERTICAL, false)
        commentsRecycler.layoutManager = commentsLayoutManager
        commentsRecycler.adapter = commentsAdapter

        detailsNestedScroll.setOnScrollChangeListener { v: NestedScrollView?, _, scrollY: Int, _, _ ->
            if (scrollY == v?.getChildAt(0)?.measuredHeight?.minus(v.measuredHeight)) {
                val size = if (commentsList.contains(null)) commentsList.size - 1 else commentsList.size
                if (canLoad && size < concert.commentsCount) {
                    canLoad = false
                    offset += limit
                    loadComments(limit, offset)
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (userUID != "" && userToken != "") {
            fab.setPrompt("Пойдёте на концерт?")
            fab.setButtonsColor(ContextCompat.getColor(ctx, R.color.colorAccent))
            fab.addItem(resources.getString(R.string.going), ContextCompat.getDrawable(ctx, R.drawable.ic_going))
            fab.addItem(resources.getString(R.string.maybe), ContextCompat.getDrawable(ctx, R.drawable.ic_maybe))
            fab.addItem(resources.getString(R.string.not_going), ContextCompat.getDrawable(ctx, R.drawable.ic_not_going))
            fab.setSelectedItem(3)
            fab.setOnItemClickListener { id: Int ->
                fab.isExpanded = false
                when (id) {
                    1 -> presenter.putToWishlist(id, userToken, userUID, "going", concert.UID)
                    2 -> presenter.putToWishlist(id, userToken, userUID, "maybe", concert.UID)
                    3 -> presenter.putToWishlist(id, userToken, userUID, "not going", concert.UID)
                }
            }
        } else {
            fab.visibility = View.GONE
        }

        Glide.with(ctx).load(concert.highresPosterURL).into(toolbarHeader)

        val datetime = DateTime(concert.datetime)
        val date = datetime.toString(DateTimeFormat.forPattern("d")) + " " +
                                    ctx.resources.getStringArray(R.array.month)[datetime.monthOfYear - 1] + " " +
                                    datetime.toString(DateTimeFormat.forPattern("yyyy"))
        infoDate.text = date
        infoTime.text = datetime.toString(DateTimeFormat.forPattern("HH:mm"))

        if (!contentLoaded) {
            buildObjectAnimator(view = detailsNestedScroll, property = View.TRANSLATION_Y, from = 800f, to = 0f).start()
            buildObjectAnimator(view = detailsNestedScroll, property = View.ALPHA, from = 0f, to = 1f).start()
            buildObjectAnimator(view = appbar, property = View.TRANSLATION_Y, from = -800f, to = 0f).start()
            buildObjectAnimator(view = appbar, property = View.ALPHA, from = 0f, to = 1f).start()

            putDummyLineUp(concert.bandsCount)
            presenter.getStatus(userToken, userUID, concert.UID)
            presenter.getLineUp(concert.lineUp)
        }

        if (concert.commentsCount > 0) loadComments(limit, offset)
        else {
            if (commentsList.contains(null)) {
                val index = commentsList.indexOf(null)
                commentsList.removeAt(index)
                commentsAdapter.notifyItemRemoved(index)
            }
            no_entries.visibility = View.VISIBLE
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        presenter.attach(this)
    }

    override fun onDetach() {
        super.onDetach()
        presenter.detach()
    }

    override fun onPause() {
        super.onPause()
        (activity as HomepageActivity).hideBottomNav(false)
    }

    override fun onResume() {
        super.onResume()
        (activity as HomepageActivity).hideBottomNav(true)
    }

    private fun putDummyLineUp(count: Int) {
        for (i in 0 until count) bandsList.add(null)
    }

    override fun onLineUpLoad(bands: ArrayList<Band>) {
        removeDummyLineUp()
        contentLoaded = true
        for (i in bands.indices) {
            bandsList.add(bands[i])
            lineUpAdapter.notifyItemChanged(i)
        }
    }

    private fun loadComments(limit: Int, offset: Int) {
        val size = if (commentsList.contains(null)) commentsList.size - 1 else commentsList.size
        if (size < concert.commentsCount) {
            canLoad = false
            commentsList.add(null)
            commentsAdapter.notifyItemInserted(commentsList.indexOf(null))
            presenter.getComments(concert.UID, limit, offset)
        }
    }

    override fun onCommentPosted(comment: Comment) {
        no_entries.visibility = View.GONE

        if (commentsList.size == concert.commentsCount) {
            commentsList.add(comment)
            commentsAdapter.notifyItemInserted(commentsList.size)
            concert.incrementCommentsCount()
        }

        sendButtonIcon.visibility = View.VISIBLE
        sendButtonLoading.visibility = View.GONE
        commentMessage.text.clear()
    }

    override fun onCommentError() {
        sendButtonIcon.visibility = View.VISIBLE
        sendButtonLoading.visibility = View.GONE
        onError()
    }

    override fun onCommentsLoad(comments: ArrayList<Comment>) {
        val size = if (commentsList.contains(null)) commentsList.size - 1 else commentsList.size
        canLoad = size < concert.commentsCount

        if (comments.size > 0) {
            if (commentsList.contains(null)) {
                val index = commentsList.indexOf(null)
                commentsList.removeAt(index)
                commentsAdapter.notifyItemRemoved(index)
            }

            for (i in comments.indices) {
                commentsList.add(comments[i])
                commentsAdapter.notifyItemInserted(commentsList.size)
            }
        } else {
            if (commentsList.contains(null)) {
                val index = commentsList.indexOf(null)
                commentsList.removeAt(index)
                commentsAdapter.notifyItemRemoved(index)
            }
        }
    }

    private fun attemptToDeleteComment(commentUID: String, adapterPosition: Int) {
        if (isEditingComment && editedCommentPosition == adapterPosition) clearEditedComment()
        presenter.deleteComment(concert.UID, commentUID, userToken, userUID, adapterPosition)
    }

    private fun attemptToPostComment() {
        val message = commentMessage.text.toString()

        if (!TextUtils.isEmpty(message)) {
            if (!isEditingComment && editedCommentUID == "" && editedCommentPosition == -1) {
                sendButtonIcon.visibility = View.GONE
                sendButtonLoading.visibility = View.VISIBLE
                presenter.sendComment(concert.UID, userToken, userUID, message)
            } else {
                presenter.editComment(message, concert.UID, editedCommentUID, userToken, userUID, editedCommentPosition)
                clearEditedComment()
            }
        }
    }

    private fun setEditedComment(commentUID: String, position: Int) {
        if (!isEditingComment) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                sendButtonIcon.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.ic_from_send_to_accept))
                val anim = sendButtonIcon.drawable as AnimatedVectorDrawable
                anim.start()
            } else {
                sendButtonIcon.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.ic_accept))
            }

            commentEditBox.visibility = View.VISIBLE
            buildObjectAnimator(view = commentEditBox, property = View.TRANSLATION_Y,
                    from = commentEditBoxDefTranslationY, to = 0f,
                    listener = object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            commentBoxShadow.visibility = View.GONE
                        }
                    }).start()
        }
        isEditingComment = true
        editedCommentUID = commentUID
        editedCommentPosition = position
        val message = commentsList[position]?.commentMessage
        commentEditText.text = message
        commentMessage.setText(message)
        commentMessage.requestFocus()
        message?.length?.let { commentMessage.setSelection(it) }
    }

    private fun clearEditedComment() {
        if (isEditingComment) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                sendButtonIcon.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.ic_from_accept_to_send))
                val anim = sendButtonIcon.drawable as AnimatedVectorDrawable
                anim.start()
            } else sendButtonIcon.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.ic_from_send_to_accept))

            buildObjectAnimator(view = commentEditBox, property = View.TRANSLATION_Y,
                    from = commentEditBoxDefTranslationY, to = 0f, duration = 150,
                    listener = object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            commentEditBox.visibility = View.INVISIBLE
                            commentBoxShadow.visibility = View.VISIBLE
                            commentEditText.text = ""
                        }
                    }).reverse()
        }

        isEditingComment = false
        editedCommentUID = ""
        editedCommentPosition = -1
        commentMessage.text.clear()
        commentMessage.clearFocus()
    }

    override fun onConcertsLoad(buttonID: Int) = fab.setSelectedItem(buttonID)

    private fun removeDummyLineUp() = bandsList.clear()

    override fun onLiked(position: Int) {
        commentsList[position]?.commentLikes?.add(userUID)
        commentsAdapter.notifyItemChanged(position)
    }

    override fun onCommentDeleted(position: Int) {
        commentsList.removeAt(position)
        commentsAdapter.notifyItemRemoved(position)
        concert.decrementCommentsCount()
        if (commentsList.size == 0) no_entries.visibility = View.VISIBLE
    }

    override fun onError() = Toast.makeText(context, "Что-то пошло не так", Toast.LENGTH_SHORT).show()

    override fun onDisliked(position: Int) {
        commentsList[position]?.commentLikes?.remove(userUID)
        commentsAdapter.notifyItemChanged(position)
    }

    override fun onCommentEdited(message: String, position: Int) {
        commentsList[position]?.commentMessage = message
        commentsAdapter.notifyItemChanged(position)
    }

    override fun onStatusLoad(status: String) {
        when (status) {
            "going" -> fab.setSelectedItem(1)
            "maybe" -> fab.setSelectedItem(2)
            "not going" -> fab.setSelectedItem(3)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(concert: Concert) = ConcertDetailsFragment().apply {
            arguments = Bundle().apply { putParcelable(BUNDLE_CONCERT, concert) }
        }
    }
}
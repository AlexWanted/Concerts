package ru.rewindforce.concerts.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.supercharge.shimmerlayout.ShimmerLayout
import org.joda.time.DateTime
import org.joda.time.Interval
import ru.rewindforce.concerts.R
import ru.rewindforce.concerts.data.Comment
import java.util.ArrayList

const val VIEW_TYPE_LOADING = 0
const val VIEW_TYPE_COMMENT = 1

class CommentsAdapter(private val commentsArrayList: ArrayList<Comment?>, private val currentUserUID: String):
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onProfileClickedListener: OnProfileClicked? = null
    private var onCommentClickedListener: OnCommentClicked? = null
    private var onActionsListener: CommentActionsInterface? = null

    fun setOnActionsListener(listener: CommentActionsInterface) {
        onActionsListener = listener
    }

    fun setOnProfileClickedListener(onProfileClickedListener: OnProfileClicked) {
        this.onProfileClickedListener = onProfileClickedListener
    }

    fun setOnCommentClickedListener(onCommentClickedListener: OnCommentClicked) {
        this.onCommentClickedListener = onCommentClickedListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = if (viewType == VIEW_TYPE_LOADING)
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_band_loading, viewGroup, false)
        else
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_comment, viewGroup, false)

        return if (viewType == VIEW_TYPE_LOADING)
            LoadingViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_band_loading, viewGroup, false))
        else
            CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_COMMENT -> {
                val comment = commentsArrayList[position] ?: Comment()
                (holder as CommentViewHolder).bind(comment, currentUserUID,
                        onActionsListener, onProfileClickedListener, onCommentClickedListener)
            }
            VIEW_TYPE_LOADING -> (holder as LoadingViewHolder).bind()
        }
    }

    override fun getItemViewType(position: Int): Int =
            if (commentsArrayList[position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_COMMENT

    override fun getItemCount(): Int = commentsArrayList.size

    interface OnProfileClicked {
        fun onProfileClicked(login: String, uid: String)
    }

    interface OnCommentClicked {
        fun onCommentClicked(UID: String, login: String, uid: String, position: Int)
    }

    interface CommentActionsInterface {
        fun onDeleteComment(commentUID: String, position: Int)
        fun onEditComment(commentUID: String, position: Int)
        fun onLikeComment(commentUID: String, position: Int)
        fun onDislikeComment(commentUID: String, position: Int)
    }

    class CommentViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        fun bind(comment: Comment, currentUserUID: String,
                 onActionsListener: CommentActionsInterface?,
                 onProfileClickedListener: OnProfileClicked?,
                 onCommentClickedListener: OnCommentClicked?) {
            val container: RelativeLayout = view.findViewById(R.id.comment_container)
            val authorAvatar: ImageView = view.findViewById(R.id.users_avatar)
            val likeIcon: ImageView = view.findViewById(R.id.like_icon)
            val login: TextView = view.findViewById(R.id.comment_author_login)
            val message: TextView = view.findViewById(R.id.comment_message)
            val datetime: TextView = view.findViewById(R.id.comment_datetime)
            val likesCount: TextView = view.findViewById(R.id.text_likes_count)
            val likeButton: LinearLayout = view.findViewById(R.id.like_button)

            if (comment.commentLikesCount > 0) likesCount.text = comment.commentLikesCount.toString()
            else likesCount.text = ""

            val haveUserLikedThis = comment.commentLikes.contains(currentUserUID)
            if (haveUserLikedThis) {
                likeButton.setOnClickListener{ onActionsListener?.onDislikeComment(comment.commentUID, adapterPosition) }
                comment.userLike = true
                likeIcon.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.ic_dislike))
                likeIcon.setColorFilter(Color.parseColor("#F44336"))
            } else {
                likeButton.setOnClickListener{ onActionsListener?.onLikeComment(comment.commentUID, adapterPosition) }
                comment.userLike = false
                likeIcon.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.ic_like))
                likeIcon.setColorFilter(Color.parseColor("#66000000"))
            }

            authorAvatar.setOnClickListener{ onProfileClickedListener?.onProfileClicked(comment.authorLogin, comment.authorUID) }
            login.setOnClickListener{ onProfileClickedListener?.onProfileClicked(comment.authorLogin, comment.authorUID) }

            login.text = comment.authorLogin
            message.text = comment.commentMessage

            val currentDateTime = DateTime().millis
            val interval: Interval = when {
                currentDateTime > comment.commentDatetime -> Interval(comment.commentDatetime, currentDateTime)
                comment.commentDatetime == currentDateTime -> Interval(0)
                else -> Interval(currentDateTime, comment.commentDatetime)
            }

            val duration = interval.toDuration()
            val time: String = if (duration.standardDays.toInt() < 1) {
                if (duration.standardHours.toInt() < 1) interval.toDuration().standardMinutes.toString() + " мин."
                else interval.toDuration().standardHours.toString() + " ч."
            } else interval.toDuration().standardDays.toString() + " дн."
            datetime.text = time

            Glide.with(view.context).load(comment.authorAvatar)
                    .thumbnail(0.1f)
                    .apply(RequestOptions().override(200))
                    .apply(RequestOptions().circleCrop())
                    .into(authorAvatar)

            container.setOnClickListener{
                onCommentClickedListener?.onCommentClicked(comment.commentUID, comment.authorLogin,
                                                           comment.authorUID, adapterPosition)
            }
        }
    }

    class LoadingViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        fun bind() {
            view.findViewById<ShimmerLayout>(R.id.info_shimmer).startShimmerAnimation()
        }
    }
}
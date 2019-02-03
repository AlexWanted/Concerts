package ru.rewindforce.concerts.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.joda.time.DateTime
import ru.rewindforce.concerts.R
import ru.rewindforce.concerts.data.Post
import ru.rewindforce.concerts.utils.getDateString
import ru.rewindforce.concerts.utils.loadCircular
import ru.rewindforce.concerts.views.ImageGridView
import java.util.ArrayList

class PostsAdapter(private val postsArrayList: ArrayList<Post>, private val currentUserUID: String): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onLikeClickListener: LikeClickListener? = null
    fun setOnLikeClickListener(likeClickListener: LikeClickListener) {
        this.onLikeClickListener = likeClickListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            PostViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_post, viewGroup, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as PostViewHolder).bind(postsArrayList[position], position, currentUserUID, onLikeClickListener)
    }

    override fun getItemCount(): Int = postsArrayList.size

    interface LikeClickListener {
        fun onLike(position: Int)
        fun onDislike(position: Int)
    }
    class PostViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        fun bind(post: Post, pos: Int, currentUserUID: String, onLikeClickListener: LikeClickListener?) {
            val avatarImage: ImageView = view.findViewById(R.id.avatar)
            val loginText: TextView = view.findViewById(R.id.login)
            val dateText: TextView = view.findViewById(R.id.date)
            val postText: TextView = view.findViewById(R.id.postText)
            val likesText: TextView = view.findViewById(R.id.likesCount)
            val commentsText: TextView = view.findViewById(R.id.commentsCount)
            val repostsText: TextView = view.findViewById(R.id.repostsCount)
            val attachmentsView: ImageGridView = view.findViewById(R.id.attachments)
            val likeButton: LinearLayout = view.findViewById(R.id.likeButton)

            var haveUserLikedThis = post.postLikes.contains(currentUserUID)

            likeButton.setOnClickListener {
                if (!haveUserLikedThis) onLikeClickListener?.onLike(pos)
                else onLikeClickListener?.onDislike(pos)
                haveUserLikedThis = post.postLikes.contains(currentUserUID)
                likesText.text = post.likesCount.toString()
                if (post.likesCount == 0) likesText.text = ""
            }

            loadCircular(view.context, post.postAuthor.avatarURL, avatarImage)
            loginText.text = post.postAuthor.login
            dateText.text = getDateString(view.context, DateTime(post.postDatetime))
            postText.text = post.postText

            if (post.getAttachments().size > 0) attachmentsView.setImages(post.getAttachments())
            else attachmentsView.visibility = View.GONE

            if (post.likesCount > 0) likesText.text = post.likesCount.toString()
            if (post.commentsCount > 0) commentsText.text = post.commentsCount.toString()
            if (post.repostsCount > 0) repostsText.text = post.repostsCount.toString()
        }
    }
}
package ru.rewindforce.concerts.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.button.MaterialButton
import io.supercharge.shimmerlayout.ShimmerLayout

import ru.rewindforce.concerts.data.Band
import ru.rewindforce.concerts.R
import ru.rewindforce.concerts.data.User

const val TYPE_LOADING: Int = 0
const val TYPE_BAND: Int = 1
const val TYPE_USER: Int = 2
const val TYPE_HEADER: Int = 3
const val TYPE_REQUESTS: Int = 4
const val TYPE_INCOMING: Int = 5
const val TYPE_OUTGOING: Int = 6

class ListAdapter(private val list: ArrayList<*>, private val type: Int):
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onItemClickListener: OnItemClicked? = null
    private var onRequestsClickListener: OnRequestsClicked? = null
    private var onActionsClickListener: OnActionsClicked? = null

    fun setOnItemClickListener(listener: OnItemClicked) { onItemClickListener = listener }
    fun setOnActionsClickListener(listener: OnActionsClicked) { onActionsClickListener = listener }
    fun setOnRequestsClickListener(listener: OnRequestsClicked) { onRequestsClickListener = listener }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = when (viewType) {
            TYPE_LOADING -> LayoutInflater.from(parent.context).inflate(R.layout.item_band_loading, parent, false)
            TYPE_BAND -> LayoutInflater.from(parent.context).inflate(R.layout.item_band, parent, false)
            TYPE_USER -> LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
            TYPE_HEADER -> LayoutInflater.from(parent.context).inflate(R.layout.item_header_appbar, parent, false)
            TYPE_REQUESTS -> LayoutInflater.from(parent.context).inflate(R.layout.item_requests_count, parent, false)
            TYPE_INCOMING, TYPE_OUTGOING -> LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
            else -> LayoutInflater.from(parent.context).inflate(R.layout.item_band_loading, parent, false)
        }

        return when (viewType) {
            TYPE_LOADING -> LoadingViewHolder(view)
            TYPE_HEADER -> HeaderViewHolder(view)
            TYPE_REQUESTS -> RequestsViewHolder(view)
            else -> ItemViewHolder(view)
        }
    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int {
        return when {
            list[position] == null -> TYPE_LOADING
            list[position] is String -> TYPE_HEADER
            list[position] is Int -> TYPE_REQUESTS
            else -> type
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_LOADING -> (holder as LoadingViewHolder).bind()
            TYPE_HEADER -> (holder as HeaderViewHolder).bind(list[position] as String)
            TYPE_REQUESTS -> (holder as RequestsViewHolder).bind(list[position].toString(), onRequestsClickListener)
            TYPE_BAND -> {
                val band: Band = list[position] as Band
                val country = band.bandCountry
                val genre = band.genre

                (holder as ItemViewHolder).bind(band.bandName, "$country, $genre", band.avatarURL,
                                                band.bandName, band.UID, onItemClickListener)
            }
            TYPE_USER, TYPE_INCOMING, TYPE_OUTGOING -> {
                val user: User = list[position] as User
                (holder as ItemViewHolder).bind(user.fullName, user.login, user.avatarURL,
                                                user.login, user.UID, onItemClickListener,
                                                onActionsClickListener)
            }
        }
    }

    interface OnItemClicked {
        fun onItemClicked(login: String, uid: String)
    }

    interface OnRequestsClicked {
        fun onRequestsClicked()
    }

    abstract class OnActionsClicked {
        open fun onAcceptClicked(pos: Int, UID: String) {}
        open fun onRejectClicked(pos: Int, UID: String) {}
        open fun onCancelClicked(pos: Int, UID: String) {}
    }

    class ItemViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        fun bind(text1: String, text2: String, imageURL: String, login: String, UID: String,
                 listener: OnItemClicked?, actionsListener: OnActionsClicked? = null) {
            val image: ImageView = view.findViewById(R.id.band_icon)
            val textView1: TextView = view.findViewById(R.id.band_name)
            val textView2: TextView = view.findViewById(R.id.band_country_and_genre)
            val container: View = view.findViewById(R.id.container)

            container.setOnClickListener { listener?.onItemClicked(login, UID) }
            textView1.text = text1
            textView2.text = text2
            Glide.with(view.context).load(imageURL)
                            .thumbnail(0.1f)
                            .apply(RequestOptions().circleCrop())
                            .into(image)

            when (itemViewType) {
                TYPE_INCOMING -> {
                    view.findViewById<View>(R.id.incomingRequestButtons).visibility = View.VISIBLE
                    view.findViewById<View>(R.id.cancelButton).visibility = View.GONE
                    val acceptButton: MaterialButton = view.findViewById(R.id.acceptButton)
                    val rejectButton: MaterialButton = view.findViewById(R.id.rejectButton)

                    acceptButton.setOnClickListener { actionsListener?.onAcceptClicked(adapterPosition, UID) }
                    rejectButton.setOnClickListener { actionsListener?.onRejectClicked(adapterPosition, UID) }
                } TYPE_OUTGOING -> {
                    view.findViewById<View>(R.id.incomingRequestButtons).visibility = View.GONE
                    val cancelButton: MaterialButton = view.findViewById(R.id.cancelButton)
                    cancelButton.visibility = View.VISIBLE

                    cancelButton.setOnClickListener { actionsListener?.onCancelClicked(adapterPosition, UID) }
                }
            }

        }
    }

    class LoadingViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        fun bind() {
            view.findViewById<ShimmerLayout>(R.id.info_shimmer).startShimmerAnimation()
        }
    }

    class HeaderViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        fun bind(text: String) {
            view.findViewById<TextView>(R.id.headerText).text = text
        }
    }

    class RequestsViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        fun bind(text: String, listener: OnRequestsClicked?) {
            view.findViewById<View>(R.id.holder).setOnClickListener { listener?.onRequestsClicked() }
            view.findViewById<TextView>(R.id.requestsCount).text = text
        }
    }
}
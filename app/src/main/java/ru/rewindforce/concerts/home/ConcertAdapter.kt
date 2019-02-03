package ru.rewindforce.concerts.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import org.joda.time.DateTime
import org.joda.time.Interval
import ru.rewindforce.concerts.R
import ru.rewindforce.concerts.data.Concert
import ru.rewindforce.concerts.utils.absoluteDateTime
import ru.rewindforce.concerts.utils.getDateString
import ru.rewindforce.concerts.utils.loadDownsize
import ru.rewindforce.concerts.utils.loadDownsizeBlurred

class ConcertAdapter(private val list: ArrayList<Concert?>, private val state: Int):
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_CONCERT = 0
        const val VIEW_TYPE_HEADER = 1

        const val STATE_PAST = 0
        const val STATE_OVERVIEW = 1
        const val STATE_UPCOMING = 2
    }

    private var onConcertClickedListener: OnConcertClicked? = null

    fun setOnConcertClickedListener(onConcertClickedListener: OnConcertClicked) {
        this.onConcertClickedListener = onConcertClickedListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = when (state) {
            STATE_OVERVIEW ->
                if (viewType == VIEW_TYPE_CONCERT) LayoutInflater.from(parent.context).inflate(R.layout.concert_item_card, parent, false)
                else LayoutInflater.from(parent.context).inflate(R.layout.concerts_header, parent, false)
            STATE_PAST, STATE_UPCOMING -> LayoutInflater.from(parent.context).inflate(R.layout.concert_item_list, parent, false)
            else -> LayoutInflater.from(parent.context).inflate(R.layout.concert_item_card, parent, false)
        }

        return if (viewType == VIEW_TYPE_HEADER) HeaderViewHolder(view) else ConcertViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_HEADER) {
            (holder as HeaderViewHolder).bind(list, onConcertClickedListener)
        } else {
            val currentHolder = holder as ConcertViewHolder
            val currentConcert = list[position] ?: Concert()
            when (state) {
                STATE_OVERVIEW -> currentHolder.bindOverview(currentConcert, onConcertClickedListener)
                STATE_PAST -> currentHolder.bindPast(currentConcert, onConcertClickedListener)
                STATE_UPCOMING -> currentHolder.bindUpcoming(currentConcert, onConcertClickedListener)
            }
        }
    }

    override fun getItemCount(): Int = list.size
    override fun getItemViewType(position: Int): Int = when (state) {
        STATE_OVERVIEW -> if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_CONCERT
        STATE_UPCOMING, STATE_PAST -> VIEW_TYPE_CONCERT
        else -> VIEW_TYPE_CONCERT
    }

    interface OnConcertClicked {
        fun onConcertClicked(concert: Concert)
    }

    class ConcertViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        fun bindOverview(concert: Concert, onConcertClickedListener: OnConcertClicked?) {
            val thumbnail: ImageView = view.findViewById(R.id.thumbnail)

            loadDownsize(view.context, concert.lowresPosterURL, thumbnail, 500)
            bind(concert, onConcertClickedListener)
        }

        fun bindUpcoming(concert: Concert, onConcertClickedListener: OnConcertClicked?) {
            val daysNumberText: TextView = view.findViewById(R.id.days_number)
            val daysWordText: TextView = view.findViewById(R.id.days_word)
            val thumbnail: ImageView = view.findViewById(R.id.thumbnail)

            val interval = Interval(absoluteDateTime(DateTime().millis), absoluteDateTime(concert.datetime))
            val intervalDays = interval.toDuration().standardDays.toInt()
            val daysWord = view.context.resources.getQuantityString(R.plurals.days_word, intervalDays, intervalDays)

            when { intervalDays > 1 -> {
                    daysNumberText.visibility = View.VISIBLE
                    daysWordText.visibility = View.VISIBLE
                    daysWordText.text = daysWord
                    daysNumberText.text = intervalDays.toString()
                } intervalDays == 1 -> {
                    daysWordText.visibility = View.GONE
                    daysNumberText.visibility = View.VISIBLE
                    daysNumberText.text = "Завтра"
                    daysNumberText.setTextColor(Color.parseColor("#ff9800"))
                } else -> {
                    daysWordText.visibility = View.GONE
                    daysNumberText.visibility = View.VISIBLE
                    daysNumberText.text = "Сегодня"
                    daysNumberText.setTextColor(Color.parseColor("#f44336"))
                }
            }

            loadDownsizeBlurred(view.context, concert.lowresPosterURL, thumbnail, 500)
            bind(concert, onConcertClickedListener)
        }

        fun bindPast(concert: Concert, onConcertClickedListener: OnConcertClicked?) {
            val daysNumberText: TextView = view.findViewById(R.id.days_number)
            val daysWordText: TextView = view.findViewById(R.id.days_word)
            val thumbnail: ImageView = view.findViewById(R.id.thumbnail)

            val interval = Interval(absoluteDateTime(concert.datetime), absoluteDateTime(DateTime().millis))
            val intervalDays = interval.toDuration().standardDays.toInt()
            val daysWord = view.context.resources.getQuantityString(R.plurals.days_word, intervalDays, intervalDays)

            daysNumberText.text = intervalDays.toString()
            daysWordText.text = daysWord
            loadDownsizeBlurred(view.context, concert.lowresPosterURL, thumbnail, 500)
            bind(concert, onConcertClickedListener)
        }

        private fun bind(concert: Concert, onConcertClickedListener: OnConcertClicked?) {
            val clubText: TextView = view.findViewById(R.id.club)
            val titleText: TextView = view.findViewById(R.id.title)
            val dateText: TextView = view.findViewById(R.id.date)
            val concertHolder: View = view.findViewById(R.id.concertHolder)

            clubText.text = concert.club
            titleText.text = concert.title
            dateText.text = getDateString(view.context, DateTime(concert.datetime))
            concertHolder.setOnClickListener { onConcertClickedListener?.onConcertClicked(concert) }
        }
    }

    class HeaderViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        fun bind(list: ArrayList<Concert?>, onConcertClickedListener: OnConcertClicked?) {
            val viewPager: ViewPager = view.findViewById(R.id.viewPager)
            val cardAdapter = PromotedConcertsPageAdapter(view.context, onConcertClickedListener)
            if (list.size > 1) {

                cardAdapter.addCardItem(list[3])
                cardAdapter.addCardItem(list[4])

                cardAdapter.addCardItem(list[1])
                cardAdapter.addCardItem(list[2])
                cardAdapter.addCardItem(list[3])
                cardAdapter.addCardItem(list[4])

                cardAdapter.addCardItem(list[1])
                cardAdapter.addCardItem(list[2])
            }

            viewPager.adapter = cardAdapter
            viewPager.currentItem = 2
            viewPager.offscreenPageLimit = 3
            viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

                override fun onPageSelected(position: Int) {}

                override fun onPageScrollStateChanged(state: Int) {
                    if (state == ViewPager.SCROLL_STATE_IDLE) {
                        val curr = viewPager.currentItem
                        val lastReal = viewPager.adapter?.count?.minus(3) ?: 0
                        if (curr <= 1) viewPager.setCurrentItem(lastReal, false)
                        else if (curr > lastReal) viewPager.setCurrentItem(2, false)
                    }
                }
            })
        }
    }
}
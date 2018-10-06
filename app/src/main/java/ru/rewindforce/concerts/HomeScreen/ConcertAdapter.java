package ru.rewindforce.concerts.HomeScreen;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;

import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import ru.rewindforce.concerts.R;

public class ConcertAdapter extends RecyclerView.Adapter<ConcertAdapter.ConcertViewHolder> {


    private final int VIEW_TYPE_PAST = 0,
                      VIEW_TYPE_OVERVIEW = 1,
                      VIEW_TYPE_UPCOMING = 2;

    private int currentViewType;
    private List<Concert> concertArrayList;
    private Context context;
    private OnConcertClicked onConcertClickedListener;
    private long currentAbsoluteDateTime;

    ConcertAdapter(Context context, List<Concert> concertList, DateTime dateTime, int type) {
        this.context = context;
        concertArrayList = concertList;
        currentViewType = type;
        currentAbsoluteDateTime = new DateTime(dateTime.getYear(), dateTime.getMonthOfYear(),
                                               dateTime.getDayOfMonth(), 0, 0).getMillis();
    }

    public void setOnConcertClickedListener(OnConcertClicked onConcertClickedListener) {
        this.onConcertClickedListener = onConcertClickedListener;
    }

    @Override
    public ConcertAdapter.ConcertViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view;
        switch (currentViewType) {
            case VIEW_TYPE_OVERVIEW: {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.concert_item_card, viewGroup, false);
                break;
            }
            case VIEW_TYPE_UPCOMING: case VIEW_TYPE_PAST: {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.concert_item_list, viewGroup, false);
                break;
            }
            default: {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.concert_item_card, viewGroup, false);
                break;
            }
        }

        return new ConcertAdapter.ConcertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ConcertAdapter.ConcertViewHolder viewHolder, int position) {
        Concert concert = concertArrayList.get(position);
        DateTime datetime = new DateTime(concert.getDatetime());
        DateTime absoluteDatetime = new DateTime(datetime.getYear(), datetime.getMonthOfYear(),
                                                 datetime.getDayOfMonth(), 0, 0);

        switch (currentViewType) {
            case VIEW_TYPE_OVERVIEW: {
                viewHolder.club.setText(concert.getClub());
                String date = datetime.toString(DateTimeFormat.forPattern("dd")) + " " +
                        context.getResources().getStringArray(R.array.month)[datetime.getMonthOfYear() - 1] + " " +
                        datetime.toString(DateTimeFormat.forPattern("yyyy")) + " в " +
                        datetime.toString(DateTimeFormat.forPattern("HH:mm"));
                viewHolder.date.setText(date);
                break;
            } case VIEW_TYPE_PAST: {
                String club_date = concert.getClub()+", "+datetime.toString(DateTimeFormat.forPattern("dd.MM.yyyy"));
                viewHolder.club.setText(club_date);
                Interval interval = new Interval(absoluteDatetime.getMillis(), currentAbsoluteDateTime);
                String days = context.getResources().getQuantityString(R.plurals.days_gone,
                        (int)interval.toDuration().getStandardDays(), (int)interval.toDuration().getStandardDays());
                viewHolder.days.setText(days);
                break;
            } case VIEW_TYPE_UPCOMING: {
                String club_date = concert.getClub()+", "+datetime.toString(DateTimeFormat.forPattern("dd.MM.yyyy"));
                viewHolder.club.setText(club_date);
                Interval interval = new Interval(currentAbsoluteDateTime, absoluteDatetime.getMillis());
                String days;
                if ((int)interval.toDuration().getStandardDays() > 1) {
                    days = context.getResources().getQuantityString(R.plurals.days_remaining,
                            (int) interval.toDuration().getStandardDays(), (int) interval.toDuration().getStandardDays());
                    viewHolder.days.setMaxLines(2);
                } else if ((int)interval.toDuration().getStandardDays() == 1) {
                    days = "Завтра";
                    viewHolder.days.setMaxLines(1);
                } else {
                    days = "Сегодня";
                    viewHolder.days.setMaxLines(1);
                }
                viewHolder.days.setText(days);
                switch ((int)interval.toDuration().getStandardDays()) {
                    case 0: case 1:
                        viewHolder.days.setTextColor(Color.parseColor("#f44336"));
                        break;
                    case 2:
                        viewHolder.days.setTextColor(Color.parseColor("#ff9800"));
                        break;
                    case 3:
                        viewHolder.days.setTextColor(Color.parseColor("#ffc107"));
                        break;
                    case 4:
                        viewHolder.days.setTextColor(Color.parseColor("#ffeb3b"));
                        break;
                    case 5:
                        viewHolder.days.setTextColor(Color.parseColor("#fff59d"));
                        break;
                }
                break;
            }
        }

        viewHolder.title.setText(concert.getBand());
        String imageURL = "http://rewindconcerts.000webhostapp.com/thumbnails/"
                            +concert.getDatetime()+"_"+concert.getId()+".webp";
        Glide.with(context).load(imageURL).thumbnail(0.1f)/*.apply(new RequestOptions().circleCrop())*/
                .into(viewHolder.thumbnail);
        ViewCompat.setTransitionName(viewHolder.thumbnail, String.valueOf(concert.getId()));
        viewHolder.concertHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConcertAdapter.this.onConcertClickedListener != null)
                    onConcertClickedListener.onConcertClicked(viewHolder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return concertArrayList.size();
    }

    public interface OnConcertClicked {
        void onConcertClicked(int position);
    }
    class ConcertViewHolder extends RecyclerView.ViewHolder {

        CardView concertHolder;
        TextView title, club, date, days;
        ImageView thumbnail;

        ConcertViewHolder(View view) {
            super(view);

            concertHolder = view.findViewById(R.id.concertHolder);
            title = view.findViewById(R.id.title);
            club = view.findViewById(R.id.club);
            if (currentViewType == VIEW_TYPE_PAST || currentViewType == VIEW_TYPE_UPCOMING)
                days = view.findViewById(R.id.days);
            if (currentViewType != VIEW_TYPE_PAST) date = view.findViewById(R.id.date);
            thumbnail = view.findViewById(R.id.thumbnail);
        }
    }
}

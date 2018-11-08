package ru.rewindforce.concerts.HomeScreen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;
import ru.rewindforce.concerts.R;

public class PromotedConcertsPageAdapter extends PagerAdapter implements CardAdapter {

    private List<CardView> cards;
    private List<Concert> concerts;
    private Context context;
    private ConcertAdapter.OnConcertClicked onConcertClickedListener;

    public PromotedConcertsPageAdapter(Context context, ConcertAdapter.OnConcertClicked onConcertClickedListener) {
        cards = new ArrayList<>();
        concerts = new ArrayList<>();
        this.context = context;
        this.onConcertClickedListener = onConcertClickedListener;
    }

    public void addCardItem(Concert item) {
        cards.add(null);
        concerts.add(item);
    }

    @Override
    public CardView getCardViewAt(int position) {
        return cards.get(position);
    }

    @Override
    public int getCount() {
        return concerts.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.concerts_header_item, container, false);
        container.addView(view);
        bindItem(concerts.get(position), view);
        CardView cardView = view.findViewById(R.id.concertHolder);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onConcertClickedListener != null)
                    onConcertClickedListener.onConcertClicked(concerts.get(position));
            }
        });
        cards.set(position, cardView);
        return view;
    }

    private void bindItem(Concert item, View view) {
        TextView title = view.findViewById(R.id.title);
        TextView club = view.findViewById(R.id.club);
        TextView date = view.findViewById(R.id.date);
        ImageView thumbnail = view.findViewById(R.id.thumbnail);

        String imageURL = "http://rewindconcerts.000webhostapp.com/thumbnails/"
                + item.getDatetime() + "_" + item.getId() + ".webp";
        DateTime datetime = new DateTime(item.getDatetime());
        String dateString = datetime.toString(DateTimeFormat.forPattern("d")) + " " +
                context.getResources().getStringArray(R.array.month)[datetime.getMonthOfYear() - 1] + " " +
                datetime.toString(DateTimeFormat.forPattern("yyyy")) + " в " +
                datetime.toString(DateTimeFormat.forPattern("HH:mm"));

        Glide.with(context).load(imageURL).thumbnail(0.1f)
                .apply(new RequestOptions().override(500)).into(thumbnail);
        title.setText(item.getBand());
        club.setText(item.getClub());
        date.setText(dateString);

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        cards.set(position, null);
    }
}


/*    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.adapter, container, false);
        container.addView(view);
        bind(mData.get(position), view);
        CardView cardView = (CardView) view.findViewById(R.id.cardView);

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);
        return view;
    }

    private void bind(CardItem item, View view) {
        TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        TextView contentTextView = (TextView) view.findViewById(R.id.contentTextView);
        titleTextView.setText(item.getTitle());
        contentTextView.setText(item.getText());
    }

}*/
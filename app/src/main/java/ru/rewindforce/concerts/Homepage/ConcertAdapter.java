package ru.rewindforce.concerts.Homepage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.List;

import ru.rewindforce.concerts.R;

public class ConcertAdapter extends RecyclerView.Adapter<ConcertAdapter.ConcertViewHolder> {

    private List<Concert> concertArrayList;
    private Context context;

    public ConcertAdapter(Context context, List<Concert> concertList) {
        this.context = context;
        concertArrayList = concertList;
    }

    @Override
    public ConcertAdapter.ConcertViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.concert_item, viewGroup, false);
        return new ConcertAdapter.ConcertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ConcertAdapter.ConcertViewHolder viewHolder, int position) {
        Concert concert = concertArrayList.get(position);
        viewHolder.title.setText(concert.getBand());
        viewHolder.club.setText(concert.getClub());

        DateTime datetime = new DateTime(concert.getDatetime());
        String date = new StringBuilder()
                .append(datetime.toString(DateTimeFormat.forPattern("dd")))
                .append(" ")
                .append(context.getResources().getStringArray(R.array.month)[datetime.getMonthOfYear()])
                .append(" ")
                .append(datetime.toString(DateTimeFormat.forPattern("yyyy"))).toString();
        viewHolder.date.setText(date);
        String imageURL = "http://rewindconcerts.000webhostapp.com/thumbnails/"
                +concert.getDatetime()+"_"+concert.getId()+".webp";
        Glide.with(context).load(imageURL).into(viewHolder.thumbnail);

    }

    @Override
    public int getItemCount() {
        return concertArrayList.size();
    }

    class ConcertViewHolder extends RecyclerView.ViewHolder {
        TextView title, club, date;
        ImageView thumbnail;
        ConcertViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            club = view.findViewById(R.id.club);
            date = view.findViewById(R.id.date);
            thumbnail = view.findViewById(R.id.thumbnail);
        }
    }
}

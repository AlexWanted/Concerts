package ru.rewindforce.concerts.HomeScreen;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import androidx.fragment.app.Fragment;
import ru.rewindforce.concerts.R;

public class ConcertDetailsFragment extends Fragment {

    private static final String ARGUMENT_CONCERT = "concert";
    private Concert concert;

    public ConcertDetailsFragment() { }

    public static ConcertDetailsFragment newInstance(Concert concert) {
        ConcertDetailsFragment fragment = new ConcertDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARGUMENT_CONCERT, concert);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            concert = (Concert) getArguments().getSerializable(ARGUMENT_CONCERT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_concert_details, container, false);

        TextView text_band = view.findViewById(R.id.text_toolbar_band_name);
        text_band.setText(concert.getBand());
        TextView text_club = view.findViewById(R.id.text_info_club_name);
        text_club.setText(concert.getClub());
        TextView text_date = view.findViewById(R.id.text_info_date);
        text_date.setText(new DateTime(concert.getDatetime()).toString(DateTimeFormat.forPattern("dd.MM.yyyy")));
        TextView text_time = view.findViewById(R.id.text_info_time);
        text_time.setText(new DateTime(concert.getDatetime()).toString(DateTimeFormat.forPattern("HH:mm")));
        TextView text_crowd = view.findViewById(R.id.text_peoples_going);
        text_crowd.setText(String.valueOf(concert.getCrowd()));

        String imageURL = "http://rewindconcerts.000webhostapp.com/thumbnails/"
                +concert.getDatetime()+"_"+concert.getId()+".webp";
        ImageView header = view.findViewById(R.id.header_bg);
        Glide.with(getContext()).load(imageURL).into(header);

        return view;
    }

}

package ru.rewindforce.concerts.ConcertDetails;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.rewindforce.concerts.Authorization.AuthorizationActivity;
import ru.rewindforce.concerts.HomeScreen.Band;
import ru.rewindforce.concerts.HomeScreen.BandAdapter;
import ru.rewindforce.concerts.HomeScreen.Concert;
import ru.rewindforce.concerts.R;
import ru.rewindforce.concerts.Views.FloatingMultiActionLayout;

public class ConcertDetailsFragment extends Fragment {

    private static final String ARGUMENT_CONCERT = "concert";
    private Concert concert;
    private FloatingMultiActionLayout fab;
    private ConcertDetailsPresenter presenter;
    private ArrayList<Band> bandArrayList;
    private BandAdapter lineUpAdapter;
    String token, uid;

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

        token = getContext().getSharedPreferences(AuthorizationActivity.PREF_NAME, Context.MODE_PRIVATE)
                .getString(AuthorizationActivity.PREF_TOKEN, "");
        uid = getContext().getSharedPreferences(AuthorizationActivity.PREF_NAME, Context.MODE_PRIVATE)
                .getString(AuthorizationActivity.PREF_UID, "");

        presenter = new ConcertDetailsPresenter();
        bandArrayList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_concert_details, container, false);

        fab = view.findViewById(R.id.fab);
        if(!uid.equals("") && !token.equals("")) {
            fab.setPrompt("Пойдёте на концерт?");
            fab.setButtonsColor(getResources().getColor(R.color.colorAccent));
            fab.addItem(getResources().getString(R.string.going), getResources().getDrawable(R.drawable.ic_going));
            fab.addItem(getResources().getString(R.string.maybe), getResources().getDrawable(R.drawable.ic_maybe));
            fab.addItem(getResources().getString(R.string.not_going), getResources().getDrawable(R.drawable.ic_not_going));
            fab.setSelectedItem(3);
            fab.setOnItemClickListener((int id) -> {
                fab.setExpanded(false);
                switch (id) {
                    case 1:
                        presenter.putToWishlist(id, token, uid, "going", concert.getId());
                        break;
                    case 2:
                        presenter.putToWishlist(id, token, uid, "maybe", concert.getId());
                        break;
                    case 3:
                        presenter.putToWishlist(id, token, uid, "not going", concert.getId());
                        break;
                }
            });
        } else {
            fab.setVisibility(View.GONE);
        }

        TextView text_band = view.findViewById(R.id.text_toolbar_band_name);
        text_band.setText(concert.getBand());
        TextView text_club = view.findViewById(R.id.text_info_club_name);
        text_club.setText(concert.getClub());
        TextView text_date = view.findViewById(R.id.text_info_date);
        text_date.setText(new DateTime(concert.getDatetime()).toString(DateTimeFormat.forPattern("dd.MM.yyyy")));
        TextView text_time = view.findViewById(R.id.text_info_time);
        text_time.setText(new DateTime(concert.getDatetime()).toString(DateTimeFormat.forPattern("HH:mm")));

        String imageURL = "http://rewindconcerts.000webhostapp.com/thumbnails/"
                +concert.getDatetime()+"_"+concert.getId()+".webp";
        ImageView header = view.findViewById(R.id.header_bg);
        Glide.with(getContext()).load(imageURL).into(header);

        RecyclerView lineUpRecycler = view.findViewById(R.id.line_up_bands);
        putDummyLineUp(concert.getBandsCount());
        lineUpAdapter = new BandAdapter(getContext(), bandArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        lineUpRecycler.setLayoutManager(linearLayoutManager);
        lineUpRecycler.setAdapter(lineUpAdapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.attachFragment(this);
        String token = getContext().getSharedPreferences(AuthorizationActivity.PREF_NAME, Context.MODE_PRIVATE)
                .getString(AuthorizationActivity.PREF_TOKEN, "");
        String uid = getContext().getSharedPreferences(AuthorizationActivity.PREF_NAME, Context.MODE_PRIVATE)
                .getString(AuthorizationActivity.PREF_UID, "");
        presenter.getStatus(token, uid, concert.getId());
        presenter.getLineUp(concert.getId());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (presenter != null) presenter.detachFragment();
    }

    void onConcertsLoad(int button_id) {
        fab.setSelectedItem(button_id);
    }
    void onLoadError(boolean shouldLoadAgain) { }

    void removeDummyLineUp(){
        bandArrayList.clear();
    }

    private void putDummyLineUp(int count) {
        for (int i = 0; i < count; i++) bandArrayList.add(null);
    }

    void onLineUpLoad(Band band, int position) {
        bandArrayList.add(band);
        lineUpAdapter.notifyItemChanged(position);
    }

    void onStatusLoad(String status) {
        switch (status) {
            case "going":
                fab.setSelectedItem(1);
                break;
            case "maybe":
                fab.setSelectedItem(2);
                break;
            case "not going":
                fab.setSelectedItem(3);
                break;
        }
    }
}

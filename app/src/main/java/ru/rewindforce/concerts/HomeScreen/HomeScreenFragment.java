package ru.rewindforce.concerts.HomeScreen;


import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.DateTime;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import ru.rewindforce.concerts.HomepageActivity;
import ru.rewindforce.concerts.R;

public class HomeScreenFragment extends Fragment implements HomepageActivity.OnTabReselected {

    private static final String TAG = HomeScreenFragment.class.getSimpleName();

    private final static String BUNDLE_LIST_STATE = "list_state",
                                BUNDLE_OFFSET = "offset",
                                BUNDLE_LIST = "concerts_list",
                                BUNDLE_TYPE = "fragment_type";

    public final static int ARG_PAST = 0,
                            ARG_OVERVIEW = 1,
                            ARG_UPCOMING = 2;

    private Parcelable listState;
    private HomeScreenPresenter presenter;
    private ConcertAdapter concertsAdapter;
    private RecyclerView concertsRecycler;
    private SwipeRefreshLayout refreshLayout;
    private boolean canLoad = true;
    private int pastVisibleItems, visibleItemCount, totalItemCount;
    private GridLayoutManager gridConcertsLayoutManager;
    private int offset = 0, count = 8;
    private int currentFragmentType = 1;
    private HomepageActivity activity;

    public HomeScreenFragment() { }

    public static HomeScreenFragment newInstance(int fragment_type) {
        HomeScreenFragment fragment = new HomeScreenFragment();
        Bundle args = new Bundle();
        args.putInt(BUNDLE_TYPE, fragment_type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getActivity() != null)
            activity = (HomepageActivity)getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            currentFragmentType = getArguments().getInt(BUNDLE_TYPE);

        presenter = new HomeScreenPresenter(currentFragmentType);
        concertsAdapter = new ConcertAdapter(activity.getApplicationContext(), presenter.concertsList,
                new DateTime(), currentFragmentType);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_concerts_overview, container, false);
        concertsRecycler = view.findViewById(R.id.concertsRecycler);
        refreshLayout = view.findViewById(R.id.refresh);
        refreshLayout.setEnabled(false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.attachFragment(this);
        gridConcertsLayoutManager = new GridLayoutManager(getContext(), currentFragmentType == ARG_OVERVIEW ? 2 : 1);
        if (currentFragmentType == ARG_OVERVIEW)
            gridConcertsLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if(position == 0) return 2;
                    else return 1;
                }
            });
        concertsRecycler.setLayoutManager(gridConcertsLayoutManager);
        concertsAdapter.setOnConcertClickedListener((Concert concert) -> activity.openConcertDetailsFragment(concert));
        concertsRecycler.setAdapter(concertsAdapter);
        if(savedInstanceState != null) {
            presenter.setConcertsList((ArrayList<Concert>) savedInstanceState.getSerializable(BUNDLE_LIST));
            offset = savedInstanceState.getInt(BUNDLE_OFFSET);
            listState = savedInstanceState.getParcelable(BUNDLE_LIST_STATE);
            gridConcertsLayoutManager.onRestoreInstanceState(listState);
        } else if ((presenter.concertsList.contains(null) && presenter.concertsList.size() == 1) ||
                    presenter.concertsList.size() == 0 ){
            startLoadingConcerts();
        }
        concertsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(dy > 0) {
                    visibleItemCount = gridConcertsLayoutManager.getChildCount();
                    totalItemCount = gridConcertsLayoutManager.getItemCount();
                    pastVisibleItems = gridConcertsLayoutManager.findFirstVisibleItemPosition();

                    if (canLoad) {
                        if ( (visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            canLoad = false;
                            offset += count;
                            startLoadingConcerts();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        listState = gridConcertsLayoutManager.onSaveInstanceState();
        outState.putParcelable(BUNDLE_LIST_STATE, listState);
        outState.putInt(BUNDLE_OFFSET, offset);
        outState.putSerializable(BUNDLE_LIST, presenter.concertsList);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (presenter != null) presenter.detachFragment();
    }

    void startLoadingConcerts() {
        refreshLayout.setRefreshing(true);
        Log.e(String.valueOf(currentFragmentType), "startLoadingConcerts");
        switch (currentFragmentType) {
            case ARG_PAST:
                presenter.loadPastConcerts(count, offset);
                break;
            case ARG_OVERVIEW: {
                presenter.loadConcerts(count, offset, "datetime", "ASC");
                break;
            }case ARG_UPCOMING:
                presenter.loadUpcomingConcerts(count, offset);
                break;
        }
    }

    void onConcertsLoad() {
        concertsAdapter.notifyItemChanged(0);
        Log.e(String.valueOf(currentFragmentType), "onConcertsLoad");
        for (int i = offset+(currentFragmentType == ARG_OVERVIEW ? 1 : 0); i < offset+count; i++)
            concertsAdapter.notifyItemInserted(i);
        canLoad = true;
        refreshLayout.setRefreshing(false);
    }

    void onLoadError(boolean shouldLoadAgain) {
        canLoad = true;
        offset -= count;
        refreshLayout.setRefreshing(false);
        if (presenter.concertsList.size() == 0 && shouldLoadAgain) {
            startLoadingConcerts();
        }
    }

    @Override
    public void onReselected() {
        if (concertsRecycler != null) concertsRecycler.smoothScrollToPosition(0);
    }
}

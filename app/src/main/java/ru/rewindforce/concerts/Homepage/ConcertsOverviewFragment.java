package ru.rewindforce.concerts.Homepage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.rewindforce.concerts.R;

public class ConcertsOverviewFragment extends Fragment {

    public ConcertsOverviewFragment() { }

    private ConcertsOverviewPresenter presenter;
    private ConcertAdapter concertsAdapter;
    private RecyclerView concertsRecycler;
    private boolean canLoad = true;
    int pastVisibleItems, visibleItemCount, totalItemCount;
    GridLayoutManager concertsLayoutManager;
    int offset = 0;

    public static ConcertsOverviewFragment newInstance() {
        return new ConcertsOverviewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new ConcertsOverviewPresenter();
        concertsAdapter = new ConcertAdapter(getActivity().getApplicationContext(), presenter.concertsList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_concerts_overview, container, false);
        concertsRecycler = view.findViewById(R.id.concertsRecycler);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        presenter.attachFragment(this);
        concertsLayoutManager = new GridLayoutManager(getContext(), 2);
        concertsLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(position == 0) return 2;
                else return 1;
            }
        });
        concertsRecycler.setLayoutManager(concertsLayoutManager);
        concertsRecycler.setAdapter(concertsAdapter);
        presenter.loadConcerts(8, offset, "concert_id", "ASC");
        concertsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if(dy > 0) //check for scroll down
                {
                    visibleItemCount = concertsLayoutManager.getChildCount();
                    totalItemCount = concertsLayoutManager.getItemCount();
                    pastVisibleItems = concertsLayoutManager.findFirstVisibleItemPosition();

                    if (canLoad)
                    {
                        if ( (visibleItemCount + pastVisibleItems) >= totalItemCount)
                        {
                            canLoad = false;
                            offset += 8;
                            presenter.loadConcerts(8, offset, "concert_id", "ASC");
                        }
                    }
                }
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    public void onConcertsLoad(List<Concert> concertsList) {
        for (int i = offset; i < offset+8; i++)
            concertsAdapter.notifyItemInserted(i);
        canLoad = true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (presenter != null) presenter.detachFragment();
    }
}

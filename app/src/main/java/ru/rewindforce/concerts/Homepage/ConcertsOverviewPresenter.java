package ru.rewindforce.concerts.Homepage;

import java.util.ArrayList;
import java.util.List;

public class ConcertsOverviewPresenter {

    private ConcertsOverviewFragment fragment;
    public List<Concert> concertsList;
    private ConcertsOverviewModel model;

    ConcertsOverviewPresenter() {
        concertsList = new ArrayList<>();
        model = new ConcertsOverviewModel();
    }

    public void attachFragment(ConcertsOverviewFragment fragment) {
        this.fragment = fragment;
    }

    public void detachFragment() {
        this.fragment = null;
    }

    public void loadConcerts(int offset, int limit, String order_by, String asc_desc) {
        model.getFlatConcertsList(offset, limit, order_by, asc_desc,
                new ConcertsOverviewModel.ConcertsCallback() {
            @Override
            public void onResponse(List<Concert> concertsList) {
                ConcertsOverviewPresenter.this.concertsList.addAll(concertsList);
                fragment.onConcertsLoad(ConcertsOverviewPresenter.this.concertsList);
            }
        });
    }
}

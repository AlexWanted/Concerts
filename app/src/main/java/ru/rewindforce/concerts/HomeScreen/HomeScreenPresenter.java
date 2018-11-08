package ru.rewindforce.concerts.HomeScreen;

import android.content.Context;

import org.joda.time.DateTime;

import java.util.ArrayList;

import ru.rewindforce.concerts.Authorization.AuthorizationActivity;

public class HomeScreenPresenter {

    private HomeScreenFragment fragment;
    private HomeScreenModel model;
    ArrayList<Concert> concertsList;

    HomeScreenPresenter(int currentFragmentType) {
        concertsList = new ArrayList<>();
        if(currentFragmentType == HomeScreenFragment.ARG_OVERVIEW) concertsList.add(null);
        model = new HomeScreenModel();
    }

    void setConcertsList(ArrayList<Concert> concertsList) {
        this.concertsList.clear();
        this.concertsList.addAll(concertsList);
    }

    void attachFragment(HomeScreenFragment fragment) {
        this.fragment = fragment;
    }

    void detachFragment() {
        this.fragment = null;
    }

    void loadUpcomingConcerts(int limit, int offset) {
        String uid = fragment.getContext().getSharedPreferences(AuthorizationActivity.PREF_NAME, Context.MODE_PRIVATE)
                .getString(AuthorizationActivity.PREF_UID, "");
        String token = fragment.getContext().getSharedPreferences(AuthorizationActivity.PREF_NAME, Context.MODE_PRIVATE)
                .getString(AuthorizationActivity.PREF_TOKEN, "");
        model.getWishlist(token, uid, limit, offset, new DateTime().getMillis(), "ASC",
                new HomeScreenModel.ConcertsCallback() {
                    @Override
                    public void onResponse(ArrayList<Concert> concertsList) {
                        if (concertsList != null) HomeScreenPresenter.this.concertsList.addAll(concertsList);
                        if (fragment != null) fragment.onConcertsLoad();
                    }
                    @Override
                    public void onError(boolean shouldLoadAgain) {
                        if (fragment != null) fragment.onLoadError(shouldLoadAgain);
                    }
                });
    }

    void loadPastConcerts(int limit, int offset) {
        String uid = fragment.getContext().getSharedPreferences(AuthorizationActivity.PREF_NAME, Context.MODE_PRIVATE)
                .getString(AuthorizationActivity.PREF_UID, "");
        String token = fragment.getContext().getSharedPreferences(AuthorizationActivity.PREF_NAME, Context.MODE_PRIVATE)
                .getString(AuthorizationActivity.PREF_TOKEN, "");
        model.getWishlist(token, uid, limit, offset, new DateTime().getMillis(), "DESC",
            new HomeScreenModel.ConcertsCallback() {
                @Override
                public void onResponse(ArrayList<Concert> concertsList) {
                    if (concertsList != null) HomeScreenPresenter.this.concertsList.addAll(concertsList);
                    if (fragment != null) fragment.onConcertsLoad();
                }
                @Override
                public void onError(boolean shouldLoadAgain) {
                    if (fragment != null) fragment.onLoadError(shouldLoadAgain);
                }
        });
    }



    void loadConcerts(int offset, int limit, String order_by, String asc_desc) {
        model.getFlatConcertsList(offset, limit, order_by, asc_desc,
                new HomeScreenModel.ConcertsCallback() {
            @Override
            public void onResponse(ArrayList<Concert> concertsList) {
                if (concertsList != null) HomeScreenPresenter.this.concertsList.addAll(concertsList);
                if (fragment != null) fragment.onConcertsLoad();
            }
            @Override
            public void onError(boolean shouldLoadAgain) {
                if (fragment != null) fragment.onLoadError(shouldLoadAgain);
            }
        });
    }
}

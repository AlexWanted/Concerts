package ru.rewindforce.concerts.HomeScreen;

import android.content.Context;
import android.util.Log;

import org.joda.time.DateTime;

import java.util.ArrayList;

import ru.rewindforce.concerts.Authorization.AuthorizationActivity;

public class HomeScreenPresenter {

    private HomeScreenFragment fragment;
    public ArrayList<Concert> concertsList;
    private HomeScreenModel model;

    HomeScreenPresenter() {
        concertsList = new ArrayList<>();
        model = new HomeScreenModel();
    }

    public void setConcertsList(ArrayList<Concert> concertsList) {
        this.concertsList.clear();
        this.concertsList.addAll(concertsList);
    }

    public void attachFragment(HomeScreenFragment fragment) {
        this.fragment = fragment;
    }

    public void detachFragment() {
        this.fragment = null;
    }

    public void loadUpcomingConcerts(int limit, int offset) {
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

    public void loadPastConcerts(int limit, int offset) {
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

    public void loadConcerts(int offset, int limit, String order_by, String asc_desc) {
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

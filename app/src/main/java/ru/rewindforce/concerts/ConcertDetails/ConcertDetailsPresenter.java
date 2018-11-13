package ru.rewindforce.concerts.ConcertDetails;

import android.util.Log;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import ru.rewindforce.concerts.HomeScreen.Band;

public class ConcertDetailsPresenter {
    private ConcertDetailsFragment fragment;
    private ConcertDetailsModel model;

    ConcertDetailsPresenter() {
        model = new ConcertDetailsModel();
    }

    void attachFragment(ConcertDetailsFragment fragment) {
        this.fragment = fragment;
    }

    void detachFragment() {
        this.fragment = null;
    }

    void getStatus(String token, String uid, int id) {
        model.getStatus(token, uid, id, new ConcertDetailsModel.ConcertsCallback() {
            @Override
            public void onStatus(String status) {
                if (fragment != null) fragment.onStatusLoad(status);
            }

            @Override
            public void onError(boolean shouldLoadAgain) {
                if (fragment != null) fragment.onLoadError(shouldLoadAgain);
            }
        });
    }

    void getLineUp(int concert_id) {
        model.getLineUp(concert_id, new ConcertDetailsModel.ConcertsCallback() {
            @Override
            public void onLineUp(ArrayList<Band> bands) {
                for(Band band : bands) if (fragment != null) fragment.onLineUpLoad(band);
            }

            @Override
            public void onError(boolean shouldLoadAgain) {
                if (fragment != null) fragment.onLoadError(shouldLoadAgain);
            }
        });
    }

    void putToWishlist(int button_id, String token, String uid, String state, int id) {
        RequestBody partUid = RequestBody.create(MediaType.parse("text/plain"), uid);
        RequestBody partState = RequestBody.create(MediaType.parse("text/plain"), state);
        RequestBody partId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(id));

        model.putToWishlist(button_id, token, partUid, partState, partId, new ConcertDetailsModel.ConcertsCallback() {
                @Override
                public void onResponse(int button_id) {
                    if (fragment != null) fragment.onConcertsLoad(button_id);
                }

                @Override
                public void onError(boolean shouldLoadAgain) {
                    if (fragment != null) fragment.onLoadError(shouldLoadAgain);
                }
            });
    }
}

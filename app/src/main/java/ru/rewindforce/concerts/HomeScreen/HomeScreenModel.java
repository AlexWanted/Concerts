package ru.rewindforce.concerts.HomeScreen;

import android.util.Log;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public class HomeScreenModel {

    private static final String TAG = HomeScreenModel.class.getSimpleName();

    private static ConcertsApi concertsApi;

    HomeScreenModel() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://rewindconcerts.000webhostapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        concertsApi = retrofit.create(ConcertsApi.class);
    }

    public void getFlatConcertsList(int limit, int offset, String order_by, String asc_desc,
                                    final ConcertsCallback callback) {
        concertsApi.getData(limit, offset, order_by, asc_desc).enqueue(new Callback<ArrayList<Concert>>() {
            @Override
            public void onResponse(Call<ArrayList<Concert>> call, Response<ArrayList<Concert>> response) {
                if (callback != null) {
                    if (response.code() == 200) callback.onResponse(response.body());
                    else if (response.code() == 204) callback.onError(false);
                    else callback.onError(true);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Concert>> call, Throwable t) {
                if (callback != null) callback.onError(true);
                Log.e("ERROR", t.getMessage());
            }
        });
    }

    void getWishlist(String token, String user_uid, int limit, int offset, long datetime,
                            String asc_desc, final ConcertsCallback callback) {
        concertsApi.getWishlist(token, user_uid, datetime, limit, offset, asc_desc).enqueue(new Callback<ArrayList<Concert>>() {
            @Override
            public void onResponse(Call<ArrayList<Concert>> call, Response<ArrayList<Concert>> response) {
                if (callback != null) {
                    if (response.code() == 200) callback.onResponse(response.body());
                    else if (response.code() == 204) callback.onError(false);
                    else callback.onError(true);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Concert>> call, Throwable t) {
                if (callback != null) callback.onError(true);
                Log.e("ERROR", t.getMessage());
            }
        });
    }

    public interface ConcertsCallback {
        void onResponse(ArrayList<Concert> concertsList);
        void onError(boolean shouldLoadAgain);
    }

    interface ConcertsApi {
        @GET("/v1/concerts")
        Call<ArrayList<Concert>> getData(@Query("limit") int limit, @Query("offset") int offset,
                                         @Query("order_by") String order_by, @Query("asc_desc") String asc_desc);

        @GET("/v1/wishlist")
        Call<ArrayList<Concert>> getWishlist(@Header("Authorization") String token,
                                             @Query("user_uid") String user_uid, @Query("datetime") long datetime,
                                             @Query("limit") int limit, @Query("offset") int offset,
                                             @Query("asc_desc") String asc_desc);
    }
}

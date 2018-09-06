package ru.rewindforce.concerts.Homepage;

import android.util.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class ConcertsOverviewModel {

    private static ConcertsApi concertsApi;
    private Retrofit retrofit;

    public ConcertsOverviewModel() {
        retrofit = new Retrofit.Builder()
                                .baseUrl("http://rewindconcerts.000webhostapp.com")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
        concertsApi = retrofit.create(ConcertsApi.class);
    }

    public void getFlatConcertsList(int offset, int limit, String order_by, String asc_desc,
                                    final ConcertsCallback callback) {
        concertsApi.getData(limit, offset, order_by, asc_desc).enqueue(new Callback<List<Concert>>() {
            @Override
            public void onResponse(Call<List<Concert>> call, Response<List<Concert>> response) {
                callback.onResponse(response.body());
            }

            @Override
            public void onFailure(Call<List<Concert>> call, Throwable t) {
                Log.d("Respone", t.getMessage());
            }
        });
    }

    public interface ConcertsCallback {
        void onResponse(List<Concert> concertsList);
    }


    interface ConcertsApi {
        @GET("/android_login_api/getFlatConcertsList.php")
        Call<List<Concert>> getData(@Query("limit") int limit, @Query("offset") int offset,
                                    @Query("order_by") String order_by, @Query("asc_desc") String asc_desc);
    }
}

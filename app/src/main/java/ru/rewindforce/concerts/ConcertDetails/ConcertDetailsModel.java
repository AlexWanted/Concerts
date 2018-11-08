package ru.rewindforce.concerts.ConcertDetails;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import ru.rewindforce.concerts.HomeScreen.Band;
import ru.rewindforce.concerts.HomeScreen.Concert;

public class ConcertDetailsModel {
    private static final String TAG = ConcertDetailsModel.class.getSimpleName();

    private static ConcertsApi concertsApi;

    ConcertDetailsModel() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://rewindconcerts.000webhostapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        concertsApi = retrofit.create(ConcertsApi.class);
    }

    void putToWishlist(final int button_id, String token, RequestBody user_uid, RequestBody state, RequestBody concert_id,
                       final ConcertsCallback callback) {
        concertsApi.putToWishlist(token, user_uid, state, concert_id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (callback != null) {
                    if (response.code() == 200) callback.onResponse(button_id);
                    else if (response.code() == 204) callback.onError(false);
                    else callback.onError(true);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (callback != null) callback.onError(true);
                Log.e("ERROR", t.getMessage());
            }
        });
    }

    void getStatus(String token, String user_uid, int concert_id, final ConcertsCallback callback) {
        concertsApi.getStatus(token, user_uid, concert_id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (callback != null) {
                    if (response.code() == 200) {
                        try {
                            callback.onStatus(response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else if (response.code() == 204) callback.onError(false);
                    else callback.onError(true);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (callback != null) callback.onError(true);
                Log.e("ERROR", t.getMessage());
            }
        });
    }

    void getLineUp(int concert_id, final ConcertsCallback callback) {
        concertsApi.getLineUp(concert_id).enqueue(new Callback<ArrayList<Band>>() {
            @Override
            public void onResponse(Call<ArrayList<Band>> call, Response<ArrayList<Band>> response) {
                if (callback != null) {
                    if (response.code() == 200) callback.onLineUp(response.body());
                    else if (response.code() == 204) callback.onError(false);
                    else callback.onError(true);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Band>> call, Throwable t) {
                if (callback != null) callback.onError(true);
                Log.e("ERROR", t.getMessage());
            }
        });
    }

    abstract static class ConcertsCallback {
        void onResponse(int button_id){}
        void onLineUp(ArrayList<Band> bands){}
        void onStatus(String status){}
        void onError(boolean shouldLoadAgain){}
    }

    interface ConcertsApi {
        @Multipart
        @POST("/v1/wishlist")
        Call<ResponseBody> putToWishlist(@Header("Authorization") String token,
                                         @Part("user_uid") RequestBody user_uid,
                                         @Part("state") RequestBody state,
                                         @Part("concert_id") RequestBody concert_id);

        @GET("/v1/wishlist")
        Call<ResponseBody> getStatus(@Header("Authorization") String token,
                                     @Query("user_uid") String user_uid,
                                     @Query("concert_id") int concert_id);

        @GET("/v1/lineup")
        Call<ArrayList<Band>> getLineUp(@Query("concert") int concert_id);
    }
}

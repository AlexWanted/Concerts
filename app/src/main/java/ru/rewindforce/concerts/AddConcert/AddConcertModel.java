package ru.rewindforce.concerts.AddConcert;

import android.util.Log;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public class AddConcertModel {

    private static AddConcertApi addConcertApi;

    public AddConcertModel() {
        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("http://192.168.43.225:5000")
                                //.baseUrl("http://rewindconcerts.000webhostapp.com")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
        addConcertApi = retrofit.create(AddConcertModel.AddConcertApi.class);
    }

    public void addConcertList(String token, String uid, RequestBody title, RequestBody[] line_up,
                               RequestBody club, RequestBody datetime,
                               MultipartBody.Part highres, MultipartBody.Part lowres,
                               final AddConcertCallback callback) {
        addConcertApi.addConcert(token, uid, title, line_up, club, RequestBody.create(MediaType.parse("text/plain"), ""), datetime, highres, lowres)
                .enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        Log.e("ERROR", response.body() != null ? response.body().string() : "");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                if (callback != null) callback.onResponse();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (callback != null) callback.onError();
                Log.e("ERROR", t.getMessage());
            }
        });
    }

    public interface AddConcertCallback {
        void onResponse();
        void onError();
    }

    interface AddConcertApi {
        @Multipart
        @POST("/concert")
        Call<ResponseBody> addConcert(@Header("Authorization") String token, @Header("UID") String uid,
                                      @Part("title") RequestBody title, @Part("line_up[]") RequestBody[] line_up,
                                      @Part("club") RequestBody club, @Part("description") RequestBody description,
                                      @Part("datetime") RequestBody datetime,
                                      @Part MultipartBody.Part highres, @Part MultipartBody.Part lowres);
    }
}

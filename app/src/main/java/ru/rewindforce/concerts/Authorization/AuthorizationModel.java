package ru.rewindforce.concerts.Authorization;

import android.util.Log;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

class AuthorizationModel {
    private static final String TAG = AuthorizationModel.class.getSimpleName();

    private static AuthorizationApi authorizationApi;

    AuthorizationModel() {
        Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl("http://rewindconcerts.000webhostapp.com")
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();
        authorizationApi = retrofit.create(AuthorizationApi.class);
    }

    void onSignIn(String login, String password, final AuthorizationContract.onSignInCallback callback) {
        authorizationApi.login(login, password).enqueue(new Callback<AuthorizationResponse>() {
            @Override
            public void onResponse(Call<AuthorizationResponse> call, Response<AuthorizationResponse> response) {
                if (callback != null) {
                    if (response.code() == 200) callback.onSignIn(response.body());
                    else callback.onError(response.code());
                }
            }

            @Override
            public void onFailure(Call<AuthorizationResponse> call, Throwable t) {
                if (callback != null) callback.onError(500);
                Log.e("ERROR", t.getMessage());
            }
        });
    }

    void onSignUp(RequestBody login, RequestBody password, RequestBody email, RequestBody firstName,
                  RequestBody lastName, RequestBody city, MultipartBody.Part avatar,
                  final AuthorizationContract.onSignUpCallback callback) {
        authorizationApi.register(login, password, email, city, firstName, lastName, avatar).enqueue(new Callback<AuthorizationResponse>() {
            @Override
            public void onResponse(Call<AuthorizationResponse> call, Response<AuthorizationResponse> response) {
                if (callback != null) {
                    if (response.code() == 201) callback.onSignUp(response.body());
                    else callback.onError(response.code());
                }
            }

            @Override
            public void onFailure(Call<AuthorizationResponse> call, Throwable t) {
                if (callback != null) callback.onError(500);
                Log.e("ERROR", t.getMessage());
            }
        });
    }

    interface AuthorizationApi {
        @GET("/v1/user")
        Call<AuthorizationResponse> login(@Query("login") String login, @Query("password") String password);

        @Multipart
        @POST("/v1/user")
        Call<AuthorizationResponse> register(@Part("login") RequestBody login, @Part("password") RequestBody password,
                                             @Part("email") RequestBody email, @Part("user_city") RequestBody city,
                                             @Part("user_firstname") RequestBody firstName,
                                             @Part("user_lastname") RequestBody lastName,
                                             @Part MultipartBody.Part avatar);
    }
}

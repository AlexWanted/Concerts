package ru.rewindforce.concerts.Authorization;


import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.rewindforce.concerts.R;
import ru.rewindforce.concerts.Authorization.AuthorizationActivity.AuthorizationApi;
import ru.rewindforce.concerts.Authorization.AuthorizationActivity.AuthorizationResponse;


public class SignInFragment extends Fragment {

    private static final String TAG = SignInFragment.class.getSimpleName();

    private TextInputEditText editLogin, editPassword;
    private static AuthorizationApi loginApi;
    private OnSignInListener onSignInListener;
    private AppCompatTextView signUp;
    private LinearLayout loadingView;
    MaterialButton buttonOffline, signIn;

    public SignInFragment() {}

    public static SignInFragment newInstance() {
        SignInFragment signInFragment = new SignInFragment();
        Bundle args = new Bundle();
        signInFragment.setArguments(args);
        return signInFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSignInListener) onSignInListener = (OnSignInListener) context;
        else  throw new RuntimeException(context.toString() + " must implement OnSignInListener");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl("http://rewindconcerts.000webhostapp.com")
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();
        loginApi = retrofit.create(AuthorizationApi.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        editLogin = view.findViewById(R.id.edit_login);
        editPassword = view.findViewById(R.id.edit_password);
        signUp = view.findViewById(R.id.sign_up);
        buttonOffline = view.findViewById(R.id.button_offline);
        loadingView = view.findViewById(R.id.loading);
        signIn = view.findViewById(R.id.button_sign_in);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            buttonOffline.setStateListAnimator(null);
            signIn.setStateListAnimator(null);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        signUp.setText(Html.fromHtml(getResources().getString(R.string.sign_up_now)));
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignInListener.onSignUp(signUp);
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onSignInListener = null;
    }

    private void attemptLogin() {
        String login = editLogin.getText().toString();
        String password = editPassword.getText().toString();

        if (!TextUtils.isEmpty(login) && !TextUtils.isEmpty(password)) {
            loadingView.setVisibility(View.VISIBLE);
            loginApi.login(login, password).enqueue(new Callback<AuthorizationResponse>() {
                @Override
                public void onResponse(Call<AuthorizationResponse> call, Response<AuthorizationResponse> response) {
                    if (response.body() != null ) {
                        if (response.body().getError() == 200) {
                            if (onSignInListener != null) {
                                onSignInListener.onStoreTokenAndUid(response.body().getToken(), response.body().getUid());
                                onSignInListener.onLogin();
                            }
                        } else {
                            Log.e("ERROR", response.body().getError() + " " + response.body().getErrorMsg());
                        }
                    }
                    loadingView.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<AuthorizationResponse> call, Throwable t) {
                    Log.e("ERROR", t.getMessage());
                    loadingView.setVisibility(View.GONE);
                }
            });
        }
    }

    public interface OnSignInListener {
        void onStoreTokenAndUid(String token, String uid);
        void onLogin();
        void onSignUp(View view);
    }
}

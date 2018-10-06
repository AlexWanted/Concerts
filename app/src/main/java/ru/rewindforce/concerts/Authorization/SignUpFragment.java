package ru.rewindforce.concerts.Authorization;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.rewindforce.concerts.R;

public class SignUpFragment extends Fragment implements CircularAnimationUtils.Dismissible {
    public SignUpFragment() {}

    private static final String BUNDLE_REVEAL = "reveal_list";
    private TextInputEditText editLogin, editPassword, editEmail, editFirstName, editLastName;
    private CircularAnimationUtils animUtils;
    private ArrayList<Integer> revealSettings;
    private MaterialButton buttonSignUp;
    private static AuthorizationActivity.AuthorizationApi registerApi;
    private SignInFragment.OnSignInListener onSignInListener;
    private LinearLayout loadingView;

    public static SignUpFragment newInstance(ArrayList<Integer> reveal) {
            SignUpFragment signUpFragment = new SignUpFragment();
            Bundle args = new Bundle();
            args.putIntegerArrayList(BUNDLE_REVEAL, reveal);
            signUpFragment.setArguments(args);
        return signUpFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SignInFragment.OnSignInListener) onSignInListener = (SignInFragment.OnSignInListener) context;
        else  throw new RuntimeException(context.toString() + " must implement OnSignInListener");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) revealSettings = getArguments().getIntegerArrayList(BUNDLE_REVEAL);
        Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl("http://rewindconcerts.000webhostapp.com")
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();
        registerApi = retrofit.create(AuthorizationActivity.AuthorizationApi.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        buttonSignUp = view.findViewById(R.id.button_sign_up);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) buttonSignUp.setStateListAnimator(null);

        editLogin = view.findViewById(R.id.edit_login);
        editPassword = view.findViewById(R.id.edit_password);
        editEmail = view.findViewById(R.id.edit_email);
        editFirstName = view.findViewById(R.id.edit_first_name);
        editLastName = view.findViewById(R.id.edit_last_name);
        loadingView = view.findViewById(R.id.loading);

        if (getActivity() != null && getContext() != null) {
            animUtils = new CircularAnimationUtils(getContext(), getActivity().getWindow(), view, revealSettings,
                    getContext().getResources().getColor(R.color.colorPrimaryDarkSignIn),
                    getContext().getResources().getColor(R.color.colorPrimaryDarkSignUp));
            animUtils.registerCircularRevealAnimation();
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSignUp();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onSignInListener = null;
    }

    private void attemptSignUp() {
        String login = editLogin.getText().toString();
        String password = editPassword.getText().toString();
        String email = editEmail.getText().toString();
        String firstName = editFirstName.getText().toString();
        String lastName = editLastName.getText().toString();

        if (!TextUtils.isEmpty(login) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(email) &&
                !TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName)) {
            loadingView.setVisibility(View.VISIBLE);
            RequestBody partLogin = RequestBody.create(MediaType.parse("text/plain"), login);
            RequestBody partPassword = RequestBody.create(MediaType.parse("text/plain"), password);
            RequestBody partEmail = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(email));
            RequestBody partFirstName = RequestBody.create(MediaType.parse("text/plain"), firstName);
            RequestBody partLastName = RequestBody.create(MediaType.parse("text/plain"), lastName);
            RequestBody partCity = RequestBody.create(MediaType.parse("text/plain"), "Москва");
            registerApi.register(partLogin, partPassword, partEmail, partCity, partFirstName, partLastName)
                    .enqueue(new Callback<AuthorizationActivity.AuthorizationResponse>() {
                @Override
                public void onResponse(Call<AuthorizationActivity.AuthorizationResponse> call, Response<AuthorizationActivity.AuthorizationResponse> response) {
                    if (response.body() != null ) {
                        if (response.body().getError() == 201) {
                            if (onSignInListener != null) {
                                onSignInListener.onStoreTokenAndUid(response.body().getToken(), response.body().getUid());
                                onSignInListener.onLogin();
                            }
                        } else {
                            Log.e("ERROR", response.body().getError() + " " + response.body().getErrorMsg());
                        }
                        if (response.raw().body() != null) response.raw().body().close();
                    }
                    loadingView.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<AuthorizationActivity.AuthorizationResponse> call, Throwable t) {
                    Log.e("ERROR", t.getMessage());
                    loadingView.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void dismiss(final OnDismissedListener listener) {
        animUtils.startCircularExitAnimation(new OnDismissedListener() {
            @Override
            public void onDismissed() {
                listener.onDismissed();
            }
        });
    }
}

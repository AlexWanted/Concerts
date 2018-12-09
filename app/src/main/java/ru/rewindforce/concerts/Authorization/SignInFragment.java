package ru.rewindforce.concerts.Authorization;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import ru.rewindforce.concerts.R;

import ru.rewindforce.concerts.Authorization.AuthorizationContract.OnSignInListener;

public class SignInFragment extends Fragment {

    private static final String TAG = SignInFragment.class.getSimpleName();

    private TextInputEditText editLogin, editPassword;
    private TextInputLayout inputLogin, inputPassword;
    private boolean isLoginValid = false, isPasswordValid = false;

    private AuthorizationContract.OnSignInListener onSignInListener;
    private AppCompatTextView signUp;
    private CircularProgressButton signIn;

    private SignInPresenter presenter;

    public SignInFragment() {}

    static SignInFragment newInstance() {
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
        presenter = new SignInPresenter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        editLogin = view.findViewById(R.id.edit_login);
        editPassword = view.findViewById(R.id.edit_password);
        signUp = view.findViewById(R.id.sign_up);
        signIn = view.findViewById(R.id.button_sign_in);
        inputLogin = view.findViewById(R.id.input_login);
        inputPassword = view.findViewById(R.id.input_password);
        MaterialButton buttonOffline = view.findViewById(R.id.button_offline);
        buttonOffline.setOnClickListener((View v) -> {if (onSignInListener != null) onSignInListener.onLogin();});
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            buttonOffline.setStateListAnimator(null);
            signIn.setStateListAnimator(null);
        }

        TextWatcher uppercaseTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Pattern uppercasePattern = Pattern.compile("^(?=.*[A-Z]+)\\S+$");
                if(uppercasePattern.matcher(s).matches()){
                    editLogin.setText(s.toString().toLowerCase());
                    editLogin.setSelection(s.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        };
        editLogin.addTextChangedListener(uppercaseTextWatcher);

        TextWatcher loginSpecTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Pattern loginPattern = Pattern.compile("^[a-zA-Z0-9._@]+$");
                if (loginPattern.matcher(s).matches()) {
                    inputLogin.setErrorEnabled(false);
                    isLoginValid = true;
                } else {
                    isLoginValid = false;
                    if(s.length() > 0) enableInputError(inputLogin, "Допускаются только латинские буквы (a-z), цифры (0-9), символы . и _");
                    else inputLogin.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        };
        editLogin.addTextChangedListener(loginSpecTextWatcher);

        TextWatcher passwordSpecTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Pattern passwordPattern = Pattern.compile("^[a-zA-Z0-9/._,:;?!*+%\\-<>@$()#\\[\\]{}\\\\]+$");
                if (passwordPattern.matcher(s).matches()) {
                    inputPassword.setErrorEnabled(false);
                    isPasswordValid = true;
                } else {
                    isPasswordValid = false;
                    if(s.length() > 0) enableInputError(inputPassword, "Допускаются только буквы латинского алфавита (A-z), " +
                                "арабских цифры (0-9) и следующие специальные символы:\n" +
                                "( ) . , : ; ? ! * + % - < > @ [ ] { } / \\ _ {} $ #");
                    else inputPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        };
        editPassword.addTextChangedListener(passwordSpecTextWatcher);

        return view;
    }

    private void enableInputError(TextInputLayout input, CharSequence message) {
        input.setError(message);
        input.setErrorEnabled(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter.attachFragment(this);

        signUp.setText(Html.fromHtml(getResources().getString(R.string.sign_up_now)));
        signUp.setOnClickListener((View v) -> onSignInListener.onSignUp(signUp));
        signIn.setOnClickListener((View v) -> attemptLogin());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onSignInListener = null;
        if (presenter != null) presenter.detachFragment();
    }

    private void attemptLogin() {
        String login = editLogin.getText().toString();
        String password = editPassword.getText().toString();

        if (!TextUtils.isEmpty(login) && !TextUtils.isEmpty(password) && isLoginValid && isPasswordValid) {
            signIn.startAnimation();
            presenter.signIn(login, password);
        }
    }

    void onSignIn(String token, String uid) {
        if (onSignInListener != null) {
            int color = getContext().getResources().getColor(R.color.done);
            Bitmap icon = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_done);
            signIn.doneLoadingAnimation(color, icon);
            new Handler().postDelayed(() -> {
                onSignInListener.onStoreTokenAndUid(token, uid);
                onSignInListener.onLogin();
            }, 500);
        }
    }

    void onError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        int color = getContext().getResources().getColor(R.color.error);
        Bitmap icon = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_error);
        signIn.doneLoadingAnimation(color, icon);
        new Handler().postDelayed(() -> signIn.revertAnimation(), 1000);
    }
}

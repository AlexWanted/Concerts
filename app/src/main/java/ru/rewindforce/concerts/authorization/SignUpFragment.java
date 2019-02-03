package ru.rewindforce.concerts.authorization;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Pattern;

import androidx.fragment.app.Fragment;
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import static ru.rewindforce.concerts.AddConcert.AddConcertPresenter.getImageActivityIntent;

import ru.rewindforce.concerts.BitmapHelper;
import ru.rewindforce.concerts.R;
import ru.rewindforce.concerts.authorization.AuthorizationContract.OnSignInListener;

public class SignUpFragment extends Fragment implements CircularAnimationUtils.Dismissible {

    private static final String TAG = SignUpFragment.class.getSimpleName();

    public SignUpFragment() {}

    private static final String BUNDLE_REVEAL = "reveal_list",
                                BUNDLE_BITMAP = "chosen_image_bitmap";
    private static final int GET_IMAGE_RESPONSE = 1;

    private ImageView profileAvatar;
    private Bitmap currentBitmap;
    private byte[] imageByteArray;
    private TextInputEditText editLogin, editPassword, editEmail, editFirstName, editLastName;
    private TextInputLayout inputLogin, inputPassword, inputEmail, inputFirstName, inputLastName;
    private CircularAnimationUtils animUtils;
    private ArrayList<Integer> revealSettings;
    private CircularProgressButton buttonSignUp;
    private OnSignInListener onSignInListener;
    private boolean isLoginValid = false, isPasswordValid = false, isEmailValid = false,
                    isFirstNameValid = false, isLastNameValid = false;

    private SignUpPresenter presenter;

    static SignUpFragment newInstance(ArrayList<Integer> reveal) {
        SignUpFragment signUpFragment = new SignUpFragment();
        Bundle args = new Bundle();
        args.putIntegerArrayList(BUNDLE_REVEAL, reveal);
        signUpFragment.setArguments(args);
        return signUpFragment;
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
        if (getArguments() != null) revealSettings = getArguments().getIntegerArrayList(BUNDLE_REVEAL);
        presenter = new SignUpPresenter(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        if (getActivity() != null && getContext() != null) {
            animUtils = new CircularAnimationUtils(getContext(), getActivity().getWindow(), view, revealSettings,
                    getContext().getResources().getColor(R.color.colorPrimaryDarkSignIn),
                    getContext().getResources().getColor(R.color.colorPrimaryDarkSignUp));
            animUtils.registerCircularRevealAnimation();
        }

        buttonSignUp = view.findViewById(R.id.button_sign_up);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) buttonSignUp.setStateListAnimator(null);

        editLogin = view.findViewById(R.id.edit_login);
        inputLogin = view.findViewById(R.id.input_login);
        editPassword = view.findViewById(R.id.edit_password);
        inputPassword = view.findViewById(R.id.input_password);
        editEmail = view.findViewById(R.id.edit_email);
        inputEmail = view.findViewById(R.id.input_email);
        editFirstName = view.findViewById(R.id.edit_first_name);
        inputFirstName = view.findViewById(R.id.input_first_name);
        editLastName = view.findViewById(R.id.edit_last_name);
        inputLastName = view.findViewById(R.id.input_last_name);
        profileAvatar = view.findViewById(R.id.profile_avatar);

        TextWatcher loginTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Pattern uppercasePattern = Pattern.compile("^(?=.*[A-Z]+)\\S+$");
                if(uppercasePattern.matcher(s).matches()){
                    editLogin.setText(s.toString().toLowerCase());
                    editLogin.setSelection(s.length());
                } else {
                    Pattern lengthPattern = Pattern.compile("^(?=.{5,20}$)\\S+$");
                    Pattern specialSymbolPattern = Pattern.compile("^(?!.*[_.]{3})\\S+$");
                    Pattern twoLetterPattern = Pattern.compile("^(?=.*[a-zA-Z]{2,})\\S+$");
                    Pattern loginPattern = Pattern.compile("^(?=.{5,20}$)(?!.*[_.]{3})(?=.*[a-zA-Z]{2,})[a-zA-Z0-9._]+$");

                    if (lengthPattern.matcher(s).matches()) {
                        inputLogin.setErrorEnabled(false);
                        isLoginValid = true;
                        if (specialSymbolPattern.matcher(s).matches()) {
                            inputLogin.setErrorEnabled(false);
                            isLoginValid = true;
                            if (twoLetterPattern.matcher(s).matches()) {
                                inputLogin.setErrorEnabled(false);
                                isLoginValid = true;
                                if (loginPattern.matcher(s).matches()) {
                                    inputLogin.setErrorEnabled(false);
                                    isLoginValid = true;
                                } else {
                                    enableInputError(inputLogin, "Допускаются только латинские буквы (a-z), цифры (0-9), символы . и _");
                                    isLoginValid = false;
                                }
                            } else {
                                enableInputError(inputLogin, "В логине должны присутсвовать как минимум две латинские буквы");
                                isLoginValid = false;
                            }
                        } else {
                            enableInputError(inputLogin, "Символы _ и . не могут присутсвовать больше 2 раз подряд");
                            isLoginValid = false;
                        }
                    } else {
                        enableInputError(inputLogin, "Логин должен быть длиной от 5 до 20 символов");
                        isLoginValid = false;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        };
        editLogin.addTextChangedListener(loginTextWatcher);

        TextWatcher passwordTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Pattern lengthPattern = Pattern.compile("^(?=.{3,}$)\\S+$");
                Pattern passwordPattern = Pattern.compile("^(?=.{3,}$)[a-zA-Z0-9/._,:;?!*+%\\-<>@$()#\\[\\]{}\\\\]+$");

                if (lengthPattern.matcher(s).matches()) {
                    inputPassword.setErrorEnabled(false);
                    isPasswordValid = true;
                    if (passwordPattern.matcher(s).matches()) {
                        inputPassword.setErrorEnabled(false);
                        isPasswordValid = true;
                    } else {
                        enableInputError(inputPassword, "Пароль может содержать буквы латинского алфавита (A-z), " +
                                                        "арабских цифры (0-9) и следующие специальные символы:\n" +
                                                        "( ) . , : ; ? ! * + % - < > @ [ ] { } / \\ _ {} $ #");
                        isPasswordValid = false;
                    }
                } else {
                    enableInputError(inputPassword, "Минимальная длина пароля - 3 символа");
                    isPasswordValid = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        };
        editPassword.addTextChangedListener(passwordTextWatcher);

        TextWatcher emailTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Pattern uppercasePattern = Pattern.compile("^(?=.*[A-Z]+)\\S+$");
                if(uppercasePattern.matcher(s).matches()){
                    editEmail.setText(s.toString().toLowerCase());
                    editEmail.setSelection(s.length());
                } else {

                    Pattern emailPattern = Pattern.compile("^\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,3}$");

                    if (emailPattern.matcher(s).matches()) {
                        inputEmail.setErrorEnabled(false);
                        isEmailValid = true;
                    } else {
                        enableInputError(inputEmail, "Неверный формат эл.адреса");
                        isEmailValid = false;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        };
        editEmail.addTextChangedListener(emailTextWatcher);

        TextWatcher firstNameTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Pattern firstPattern = Pattern.compile("[A-Za-zА-Яа-яЁё]+");

                if (firstPattern.matcher(s).matches()) {
                    inputFirstName.setErrorEnabled(false);
                    isFirstNameValid = true;
                } else {
                    enableInputError(inputFirstName, "Имя может содержать только латинские (A-z) и кириллические (А-я) буквы");
                    isFirstNameValid = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        };
        editFirstName.addTextChangedListener(firstNameTextWatcher);

        TextWatcher lastNameTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Pattern firstPattern = Pattern.compile("[A-Za-zА-Яа-яЁё]+");

                if (firstPattern.matcher(s).matches()) {
                    inputLastName.setErrorEnabled(false);
                    isLastNameValid = true;
                } else {
                    enableInputError(inputLastName, "Имя может содержать только латинские (A-z) и кириллические (А-я) буквы");
                    isLastNameValid = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        };
        editLastName.addTextChangedListener(lastNameTextWatcher);

        profileAvatar.setOnClickListener((View v) -> startActivityForResult(getImageActivityIntent(),
                                                                            GET_IMAGE_RESPONSE));

        view.findViewById(R.id.button_close).setOnClickListener((View v) ->
            dismiss(() -> getFragmentManager().beginTransaction().remove(this).commitNowAllowingStateLoss())
        );

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_IMAGE_RESPONSE) {
            if (data != null && data.getData() != null) {
                try {
                    if (getContext() != null) {
                        InputStream inputStream = getContext().getContentResolver().openInputStream(data.getData());
                        currentBitmap = BitmapFactory.decodeStream(inputStream);
                        imageByteArray = BitmapHelper.getCompressedBitmapData(currentBitmap, 1000000, 1000);
                        currentBitmap = null;
                        /*ByteArrayOutputStream out = new ByteArrayOutputStream();
                        currentBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        imageByteArray = out.toByteArray();
                        out.flush();
                        Log.e("SIZE", String.valueOf(imageByteArray.length));*/
                        Glide.with(getContext()).load(BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length)).thumbnail(0.1f)
                                .apply(new RequestOptions().circleCrop()).into(profileAvatar);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
        }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonSignUp.setOnClickListener((View v) -> attemptSignUp());
        presenter.attachFragment(this);

        if(savedInstanceState != null) {
            imageByteArray = savedInstanceState.getByteArray(BUNDLE_BITMAP);
            if (imageByteArray != null) {
                currentBitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
                Log.e("SIZE 2", String.valueOf(currentBitmap.getByteCount()));
                Glide.with(getContext()).load(currentBitmap).thumbnail(0.1f)
                        .apply(new RequestOptions().circleCrop()).into(profileAvatar);
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onSignInListener = null;
        if (presenter != null) presenter.detachFragment();
    }

    public static void enableInputError(TextInputLayout input, CharSequence message) {
        input.setError(message);
        input.setErrorEnabled(true);
    }

    private void attemptSignUp() {
        String login = editLogin.getText().toString();
        String password = editPassword.getText().toString();
        String email = editEmail.getText().toString();
        String firstName = editFirstName.getText().toString();
        String lastName = editLastName.getText().toString();

        if(TextUtils.isEmpty(login)) enableInputError(inputLogin, "Это обязательное поле");
        if(TextUtils.isEmpty(password)) enableInputError(inputPassword, "Это обязательное поле");
        if(TextUtils.isEmpty(email)) enableInputError(inputEmail, "Это обязательное поле");
        if(TextUtils.isEmpty(firstName)) enableInputError(inputFirstName, "Это обязательное поле");
        if(TextUtils.isEmpty(lastName)) enableInputError(inputLastName, "Это обязательное поле");

        if (!TextUtils.isEmpty(login) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName)) {
            if(isLoginValid && isPasswordValid && isEmailValid && isFirstNameValid && isLastNameValid) {
                buttonSignUp.startAnimation();
                presenter.signUp(login, password, email, firstName, lastName, imageByteArray);
            }
        }
    }

    void onSignUp(String token, String uid, String login, String role) {
        if (onSignInListener != null) {
            int color = getContext().getResources().getColor(R.color.done);
            Bitmap icon = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_done);
            buttonSignUp.doneLoadingAnimation(color, icon);
            new Handler().postDelayed(() -> {
                onSignInListener.onStoreTokenAndUid(token, uid, login, role);
                onSignInListener.onLogin(); }, 500);
        }
    }

    void onError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        int color = getContext().getResources().getColor(R.color.error);
        Bitmap icon = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_error);
        buttonSignUp.doneLoadingAnimation(color, icon);
        new Handler().postDelayed(() -> buttonSignUp.revertAnimation(), 1000);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");

        if (imageByteArray != null && imageByteArray.length < 1000000) outState.putByteArray(BUNDLE_BITMAP, imageByteArray);
    }

    @Override
    public void dismiss(OnDismissedListener listener) {
        animUtils.startCircularExitAnimation(listener);
    }
}

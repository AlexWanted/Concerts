package ru.rewindforce.concerts.authorization;

import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import ru.rewindforce.concerts.HomepageActivity;
import ru.rewindforce.concerts.R;

public class AuthorizationActivity extends AppCompatActivity implements AuthorizationContract.OnSignInListener {

    private static final String TAG = AuthorizationActivity.class.getSimpleName();

    public static final String  SIGN_IN_FRAGMENT = "sing_in",
                                 SIGN_UP_FRAGMENT = "sing_up";

    public static final String PREF_NAME = "concerts_prefs",
                               PREF_TOKEN = "pref_token",
                               PREF_UID = "pref_uid",
                               PREF_LOGIN = "pref_login",
                               PREF_ROLE = "pref_role",
                               PREF_STATUS = "pref_status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.colorPrimaryDarkSignIn));
        }
        /*getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().remove(PREF_TOKEN).apply();
        getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().remove(PREF_UID).apply();*/
        if (hasToken() && hasUid()) {
            startActivity(new Intent(AuthorizationActivity.this, HomepageActivity. class));
            finish();
        } else {
            if(getSupportFragmentManager().findFragmentByTag(SIGN_IN_FRAGMENT) == null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment signInFragment = SignInFragment.newInstance();
                ft.add(R.id.authorization_container, signInFragment, SIGN_IN_FRAGMENT);
                ft.commit();
            }
        }
    }

    public void attachSignUpFragment(View view) {
        if(getSupportFragmentManager().findFragmentByTag(SIGN_UP_FRAGMENT) == null) {
            ArrayList<Integer> reveal = new ArrayList<>();
            reveal.add((int) (view.getX() + view.getWidth() / 2));
            reveal.add(findViewById(R.id.authorization_container).getBottom());
            reveal.add(findViewById(R.id.authorization_container).getWidth());
            reveal.add(findViewById(R.id.authorization_container).getHeight());
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment signInFragment = SignUpFragment.newInstance(reveal);
            ft.add(R.id.authorization_container, signInFragment, SIGN_UP_FRAGMENT);
            ft.commit();
        }
    }

    private boolean hasToken() {
        return getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).contains(PREF_TOKEN);
    }

    private boolean hasUid() {
        return getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).contains(PREF_UID);
    }

    @Override
    public void onStoreTokenAndUid(String token, String uid, String login, String role) {
        getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().putString(PREF_TOKEN, token).apply();
        getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().putString(PREF_UID, uid).apply();
        getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().putString(PREF_LOGIN, login.toLowerCase()).apply();
        getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().putString(PREF_ROLE, role).apply();
        //getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().putString(PREF_STATUS, "online").apply();
    }

    @Override
    public void onLogin() {
        Log.e("TOKEN", getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(PREF_TOKEN, ""));
        startActivity(new Intent(AuthorizationActivity.this, HomepageActivity.class));
        finish();
    }

    @Override
    public void onSignUp(View view) {
        attachSignUpFragment(view);
    }

    @Override
    public void onBackPressed() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(SIGN_UP_FRAGMENT);
        if(fragment != null) {
            ((CircularAnimationUtils.Dismissible) fragment).dismiss(() ->
                    getSupportFragmentManager().beginTransaction()
                                               .remove(fragment)
                                               .commitNowAllowingStateLoss());
        } else {
            super.onBackPressed();
        }
    }
}


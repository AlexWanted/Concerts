package ru.rewindforce.concerts.authorization;

import android.view.View;

import ru.rewindforce.concerts.data.Credentials;

class AuthorizationContract {

    interface onSignInCallback {
        void onSignIn(Credentials response);
        void onError(int errorCode);
    }

    interface onSignUpCallback {
        void onSignUp(Credentials response);
        void onError(int errorCode);
    }

    public interface OnSignInListener {
        void onStoreTokenAndUid(String token, String uid, String login, String role);
        void onLogin();
        void onSignUp(View view);
    }
}

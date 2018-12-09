package ru.rewindforce.concerts.Authorization;

import android.view.View;

class AuthorizationContract {

    interface onSignInCallback {
        void onSignIn(AuthorizationResponse response);
        void onError(int errorCode);
    }

    interface onSignUpCallback {
        void onSignUp(AuthorizationResponse response);
        void onError(int errorCode);
    }

    public interface OnSignInListener {
        void onStoreTokenAndUid(String token, String uid);
        void onLogin();
        void onSignUp(View view);
    }
}

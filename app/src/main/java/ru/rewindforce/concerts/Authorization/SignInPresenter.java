package ru.rewindforce.concerts.Authorization;

class SignInPresenter {

    private SignInFragment fragment;
    private AuthorizationModel model;

    SignInPresenter() {
        model = new AuthorizationModel();
    }

    void attachFragment(SignInFragment fragment) {
        this.fragment = fragment;
    }

    void detachFragment() {
        this.fragment = null;
    }

    void signIn(String login, String password) {
        model.onSignIn(login, password, new AuthorizationContract.onSignInCallback() {
            @Override
            public void onSignIn(AuthorizationResponse response) {
                if(fragment != null) fragment.onSignIn(response.getToken(), response.getUid());
            }

            @Override
            public void onError(int errorCode) {
                if(fragment != null) {
                    switch (errorCode) {
                        case 400:
                            fragment.onError("Неверный запрос");
                            break;
                        case 404:
                            fragment.onError("Неверный логин или пароль");
                            break;
                        case 500:
                            fragment.onError("Произошла неизвестная ошибка, попробуйте ещё раз");
                            break;
                        default:
                            fragment.onError("Произошла неизвестная ошибка, попробуйте ещё раз");
                            break;
                    }
                }
            }
        });
    }
}

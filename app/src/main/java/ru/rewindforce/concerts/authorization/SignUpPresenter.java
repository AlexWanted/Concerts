package ru.rewindforce.concerts.authorization;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import ru.rewindforce.concerts.data.Credentials;

public class SignUpPresenter {
    private SignUpFragment fragment;
    private AuthorizationModel model;
    private Context context;

    SignUpPresenter(Context context) {
        model = new AuthorizationModel();
        this.context = context;
    }

    void attachFragment(SignUpFragment fragment) {
        this.fragment = fragment;
    }

    void detachFragment() {
        fragment = null;
        context = null;
    }

    void signUp(String login, String password, String email, String firstName, String lastName, byte[] imageByteArray) {
        RequestBody partLogin = RequestBody.create(MediaType.parse("text/plain"), login);
        RequestBody partPassword = RequestBody.create(MediaType.parse("text/plain"), password);
        RequestBody partEmail = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(email));
        RequestBody partFirstName = RequestBody.create(MediaType.parse("text/plain"), firstName);
        RequestBody partLastName = RequestBody.create(MediaType.parse("text/plain"), lastName);
        RequestBody partCity = RequestBody.create(MediaType.parse("text/plain"), "Москва");

        RequestBody reqFile;
        MultipartBody.Part avatar;
        if(imageByteArray != null) {
            File file = new File(context.getCacheDir(), "tempavatar.jpg");
            if (file.exists()) file.delete();
            try {
                boolean isCreated = file.createNewFile();
                Log.d("FILE", "Is File Created: " + isCreated);
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(imageByteArray);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                Log.e("ERROR", e.getMessage());
                e.printStackTrace();
            }
            reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            avatar = MultipartBody.Part.createFormData("avatar", file.getName(), reqFile);
        } else {
            reqFile = RequestBody.create(MultipartBody.FORM,"");
            avatar = MultipartBody.Part.createFormData("file","",reqFile);
        }

        model.onSignUp(partLogin, partPassword, partEmail, partFirstName, partLastName, partCity, avatar,
                new AuthorizationContract.onSignUpCallback() {
            @Override
            public void onSignUp(Credentials response) {
                if(fragment != null) fragment.onSignUp(response.getToken(), response.getUid(), response.getLogin(), response.getRole());
            }

            @Override
            public void onError(int errorCode) {
                if(fragment != null) {
                    switch (errorCode) {
                        case 400:
                            fragment.onError("Неверный запрос");
                            break;
                        case 409:
                            fragment.onError("Пользователь с таким логином или эл.почтой уже существует");
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

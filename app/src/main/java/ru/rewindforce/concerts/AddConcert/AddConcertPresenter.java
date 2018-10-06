package ru.rewindforce.concerts.AddConcert;


import android.content.Intent;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class AddConcertPresenter {
    private AddConcertFragment fragment;
    private AddConcertModel model;

    public AddConcertPresenter() {
        model = new AddConcertModel();
    }

    public void attachFragment(AddConcertFragment fragment) {
        this.fragment = fragment;
    }

    public void detachFragment() {
        this.fragment = null;
    }

    public Intent getImageActivityIntent() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Выберите приложение");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
        return chooserIntent;
    }

    public void addConcert(String token, String uid,
                           String band, String club, long datetime, byte[] imageByteArray) {
        if (fragment != null) {
            File file = new File(fragment.getContext().getCacheDir(), "tempimage.webp");
            if (file.exists()) file.delete();
            try {
                boolean isCreated = file.createNewFile();
                Log.d("FILE", "Is File Created: "+isCreated);
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(imageByteArray);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                Log.e("ERROR", e.getMessage());
                e.printStackTrace();
            }

            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), reqFile);
            RequestBody partBand = RequestBody.create(MediaType.parse("text/plain"), band);
            RequestBody partClub = RequestBody.create(MediaType.parse("text/plain"), club);
            RequestBody partDatetime = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(datetime));
            RequestBody partUid = RequestBody.create(MediaType.parse("text/plain"), uid);

            model.addConcertList(token, partUid, partBand, partClub,
                    partDatetime, body, new AddConcertModel.AddConcertCallback() {
                    @Override
                    public void onResponse() {
                        if (fragment != null && fragment.getActivity() != null)
                            fragment.getActivity().getSupportFragmentManager().popBackStack();
                    }

                    @Override
                    public void onError() {
                    }
                });
        }
    }
}

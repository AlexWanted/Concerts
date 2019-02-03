package ru.rewindforce.concerts.AddConcert;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class AddConcertPresenter {
    private AddConcertFragment fragment;
    private AddConcertModel model;
    private Context context;

    public AddConcertPresenter(Context context) {
        this.context = context;
        model = new AddConcertModel();
    }

    public void attachFragment(AddConcertFragment fragment) {
        this.fragment = fragment;
    }

    public void detachFragment() {
        this.fragment = null;
    }

    public static Intent getImageActivityIntent() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Выберите приложение");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
        return chooserIntent;
    }

    void addConcert(String token, String uid, String title, String[] line_up, String club, long datetime, byte[] highresByteArray,
                    byte[] lowresByteArray) {
        if (fragment != null) {

            RequestBody reqHighresFile;
            MultipartBody.Part highresPoster;
            if(highresByteArray != null) {
                File file = new File(context.getCacheDir(), "highres_poster.jpg");
                if (file.exists()) file.delete();
                try {
                    boolean isCreated = file.createNewFile();
                    Log.d("FILE", "Is File Created: " + isCreated);
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(highresByteArray);
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    Log.e("ERROR", e.getMessage());
                    e.printStackTrace();
                }
                reqHighresFile = RequestBody.create(MediaType.parse("image/*"), file);
                highresPoster = MultipartBody.Part.createFormData("highres_poster", file.getName(), reqHighresFile);
            } else {
                reqHighresFile = RequestBody.create(MultipartBody.FORM,"");
                highresPoster = MultipartBody.Part.createFormData("file","",reqHighresFile);
            }

            RequestBody reqLowresFile;
            MultipartBody.Part lowresPoster;
            if(lowresByteArray != null) {
                File file = new File(context.getCacheDir(), "lowres_poster.jpg");
                if (file.exists()) file.delete();
                try {
                    boolean isCreated = file.createNewFile();
                    Log.d("FILE", "Is File Created: " + isCreated);
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(lowresByteArray);
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    Log.e("ERROR", e.getMessage());
                    e.printStackTrace();
                }
                reqLowresFile = RequestBody.create(MediaType.parse("image/*"), file);
                lowresPoster = MultipartBody.Part.createFormData("lowres_poster", file.getName(), reqLowresFile);
            } else {
                reqLowresFile = RequestBody.create(MultipartBody.FORM,"");
                lowresPoster = MultipartBody.Part.createFormData("file","",reqLowresFile);
            }

            /*File file = new File(fragment.getContext().getCacheDir(), "tempimage.webp");
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
            }*/

            //RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            //MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), reqFile);
            RequestBody[] partLineUp = new RequestBody[line_up.length];
            for (int i = 0; i < line_up.length; i++) {
                partLineUp[i] = RequestBody.create(MediaType.parse("text/plain"), line_up[i]);
            }

            RequestBody partTitle = RequestBody.create(MediaType.parse("text/plain"), title);
            RequestBody partClub = RequestBody.create(MediaType.parse("text/plain"), club);
            RequestBody partDatetime = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(datetime));

            model.addConcertList(token, uid, partTitle, partLineUp, partClub, partDatetime, highresPoster,
                    lowresPoster, new AddConcertModel.AddConcertCallback() {
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

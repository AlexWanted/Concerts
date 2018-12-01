package ru.rewindforce.concerts.HomeScreen;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import ru.rewindforce.concerts.R;

public class Band {
    private int band_id;
    private String band_name, band_country, band_genre;

    public int getId() { return band_id; }
    String getBandName() { return band_name; }
    String getBandCountry() { return band_country; }
    String getBandGenre() { return band_genre; }

    void loadBandAvatar(Context context, ImageView view) {
        String imageURL = "https://rewindconcerts.000webhostapp.com/bands/avatars/lowres/"
                + md5(String.valueOf(getId()))+".jpg";
        RequestBuilder<Drawable> thumbnailRequest = Glide.with(context).load(context.getResources().getDrawable(R.drawable.ic_band));

        Glide.with(context).load(imageURL).thumbnail(thumbnailRequest)
                .apply(new RequestOptions().circleCrop()).into(view);
    }

    private static String md5(final String message) {
        final String MD5 = "MD5";
        try {
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(message.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2) h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}

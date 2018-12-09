package ru.rewindforce.concerts;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

public class BitmapHelper {
    public static byte[] getCompressedBitmapData(Bitmap bitmap, int maxFileSize, int maxDimensions) {
        Bitmap resizedBitmap;
        if (bitmap.getWidth() > maxDimensions || bitmap.getHeight() > maxDimensions) {
            resizedBitmap = getResizedBitmap(bitmap, maxDimensions);
        } else {
            resizedBitmap = bitmap;
        }

        byte[] bitmapData = getByteArray(resizedBitmap, 100);

        while (bitmapData.length > maxFileSize) {
            bitmapData = getByteArray(resizedBitmap, 90);
        }
        return bitmapData;
    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image,
                width,
                height,
                true);
    }

    public static byte[] getByteArray(Bitmap bitmap, int compress) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, compress, bos);

        return bos.toByteArray();
    }
}

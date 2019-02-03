package ru.rewindforce.concerts.views;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class MetricUtils {

    public static int dpToPx(float dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    public static int pxToDp(int px){
        return Math.round(px/(Resources.getSystem().getDisplayMetrics().xdpi/DisplayMetrics.DENSITY_DEFAULT));
    }
}

package ru.rewindforce.concerts.authorization;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;

import com.google.android.material.animation.ArgbEvaluatorCompat;

import java.util.ArrayList;

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import ru.rewindforce.concerts.R;

public class CircularAnimationUtils {
    private Context context;
    private Window window;
    private View view;
    private ArrayList<Integer> revealSettings;
    private int startColor, endColor;
    private boolean isAnimationRunning;

    public CircularAnimationUtils(Context context, Window window, View view,
                                  ArrayList<Integer> revealSettings, int startColor, int endColor) {
        this.context = context;
        this.window = window;
        this.view = view;
        this.revealSettings = revealSettings;
        this.startColor = startColor;
        this.endColor = endColor;
        isAnimationRunning = false;
    }


    public void registerCircularRevealAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    v.removeOnLayoutChangeListener(this);
                    int cx = revealSettings.get(0);
                    int cy = revealSettings.get(1);
                    int width = revealSettings.get(2);
                    int height = revealSettings.get(3);
                    int duration = context.getResources().getInteger(android.R.integer.config_mediumAnimTime);

                    float finalRadius = (float) Math.sqrt(width * width + height * height);
                    Animator anim = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0, finalRadius).setDuration(duration);
                    anim.setInterpolator(new FastOutSlowInInterpolator());
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            isAnimationRunning = true;
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            super.onAnimationCancel(animation);
                            isAnimationRunning = false;
                            changeWindowBgColor(window, context.getResources().getColor(R.color.colorPrimaryDarkSignIn));
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            isAnimationRunning = false;
                            changeWindowBgColor(window, context.getResources().getColor(R.color.colorPrimaryDarkSignUp));
                        }
                    });
                    if (!isAnimationRunning) {
                        anim.start();
                        startColorAnimation(view, startColor, endColor, duration);
                    }
                }
            });
        }
    }

    public void startCircularExitAnimation(final Dismissible.OnDismissedListener listener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int cx = revealSettings.get(0);
            int cy = revealSettings.get(1);
            int width = revealSettings.get(2);
            int height = revealSettings.get(3);
            int duration = context.getResources().getInteger(android.R.integer.config_mediumAnimTime);

            float initRadius = (float) Math.sqrt(width * width + height * height);
            Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initRadius, 0);
            anim.setDuration(duration);
            anim.setInterpolator(new FastOutSlowInInterpolator());
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    isAnimationRunning = true;
                    changeWindowBgColor(window, context.getResources().getColor(R.color.colorPrimaryDarkSignIn));
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    isAnimationRunning = false;
                    changeWindowBgColor(window, context.getResources().getColor(R.color.colorPrimaryDarkSignIn));
                    if(listener != null) listener.onDismissed();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    isAnimationRunning = false;
                }
            });
            if (!isAnimationRunning) {
                anim.start();
                startColorAnimation(view, endColor, startColor, duration);
            }
        } else {
            if(listener != null) listener.onDismissed();
        }
    }

    private static void changeWindowBgColor(Window window, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.getDecorView().setBackgroundColor(color);
        }
    }

    interface Dismissible {
        interface OnDismissedListener { void onDismissed();}
        void dismiss(OnDismissedListener listener);
    }

    private void startColorAnimation(final View view, int startColor, int endColor, int duration) {
        ValueAnimator anim = new ValueAnimator();
        anim.setIntValues(startColor, endColor);
        anim.setEvaluator(new ArgbEvaluatorCompat());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setBackgroundColor((Integer) valueAnimator.getAnimatedValue());
            }
        });
        anim.setDuration(duration);
        anim.start();
    }
}
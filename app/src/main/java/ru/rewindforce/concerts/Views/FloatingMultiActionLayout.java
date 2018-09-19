package ru.rewindforce.concerts.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class FloatingMultiActionLayout extends ViewGroup{

    public static final int GRAVITY_START = 0, GRAVITY_END = 2;

    private int mGravity;

    private int mItemCount;


    public FloatingMultiActionLayout(Context context) {
        this(context, null);
    }

    public FloatingMultiActionLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingMultiActionLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL){
            if (mGravity == GRAVITY_START) mGravity = GRAVITY_END;
            else mGravity = GRAVITY_START;
        }


    }


    public int addItem(FloatingActionButton item) {
        int id = mItemCount;
        addView(item);
        requestLayout();
        return id;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = getResources().getDisplayMetrics().widthPixels;
        int h = getResources().getDisplayMetrics().heightPixels;
        setMeasuredDimension(w, h);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mItemCount = 0;
        for (int i=0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof FloatingActionButton){

                mItemCount++;


            }
        }

    }
}

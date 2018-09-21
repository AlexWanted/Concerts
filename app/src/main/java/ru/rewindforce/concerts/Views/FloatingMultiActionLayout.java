package ru.rewindforce.concerts.Views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ru.rewindforce.concerts.R;

public class FloatingMultiActionLayout extends ViewGroup{

    private static final String TAG = FloatingMultiActionLayout.class.getSimpleName();

    public static final int GRAVITY_START = 0, GRAVITY_END = 2;

    private Context mContext;
    private int mGravity;


    private ArrayList<FloatingActionButton> mItemList;
    private int mItemCount;
    private OnItemClickListener mOnItemClickListener;

    private boolean isExpanded = true;

    private ColorDrawable mBackground;
    //Buttons parameters:
    private int mButtonsColor;
    private float mButtonsElevation;

    public FloatingMultiActionLayout(Context context) {
        this(context, null);
    }

    public FloatingMultiActionLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingMultiActionLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;
        mItemList = new ArrayList<>();

        if (attrs != null){
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FloatingActionButton, defStyleAttr, 0);
            mGravity = a.getInt(R.styleable.FloatingActionButton_ButtonGravity, GRAVITY_END);

        }

        if (getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL){
            if (mGravity == GRAVITY_START) mGravity = GRAVITY_END;
            else mGravity = GRAVITY_START;
        }

        mBackground = new ColorDrawable(Color.BLACK);
        mBackground.setAlpha(0);
        setBackground(mBackground);

        setExpanded(false);
    }

    public void setExpanded(boolean toExpand){
        long expandAnimDuration = 80;
        isExpanded = toExpand;
        for (final View view: mItemList){
            if (mItemList.indexOf(view) != mItemCount-1) {
                View mainButton = mItemList.get(mItemCount - 1);
                if (isExpanded) {
                    view.animate()
                            .translationY(0)
                            .setDuration(expandAnimDuration)
                            .withStartAction(new Runnable() {
                                @Override
                                public void run() {
                                    view.setVisibility(VISIBLE);
                                }
                            })
                            .start();
                } else {
                    view.animate()
                            .translationY(mainButton.getY() - view.getY() + mainButton.getMeasuredHeight() / 2 - view.getMeasuredHeight()/2)
                            .setDuration(expandAnimDuration)
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    view.setVisibility(INVISIBLE);
                                }
                            })
                            .start();
                }
            }
        }
        int alpha = mBackground.getAlpha();
        ValueAnimator bgColorAnimator = ValueAnimator.ofInt(alpha, isExpanded ? 200 : 0);
        bgColorAnimator.setDuration(expandAnimDuration);
        bgColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mBackground.setAlpha((int)animation.getAnimatedValue());
                //setBackground(mBackground);
            }
        });
        bgColorAnimator.start();
    }

    public boolean isExpanded(){
        return isExpanded;
    }

    public int addItem(FloatingActionButton item) {
        int id = mItemCount;
        addView(item);
        requestLayout();
        return id;
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mOnItemClickListener = listener;
    }



    public void setButtonsColor(@ColorInt int color){
        if (mButtonsColor == color) return;
        mButtonsColor = color;
        requestLayout();
    }

    public void setButtonsElevation(float elevation){
        if (mButtonsElevation == elevation) return;
        mButtonsElevation = elevation;
        requestLayout();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
                View.resolveSize(0, widthMeasureSpec),
                View.resolveSize(0, heightMeasureSpec));
        mItemList.clear();
        mItemCount = 0;
        for (int i=0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof FloatingActionButton){
                mItemList.add((FloatingActionButton)child);
                mItemCount++;
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i=mItemCount-1; i>=0; i--){
            View child = mItemList.get(i);

            int centerX;
            if (mGravity == GRAVITY_START) centerX = getLeft() + getPaddingLeft() + mItemList.get(mItemCount-1).getMeasuredWidth()/2;
            else centerX = getRight() - getPaddingRight() - mItemList.get(mItemCount-1).getMeasuredWidth()/2;

            int centerY = i==mItemCount-1 ? getBottom()-getPaddingBottom() - child.getMeasuredHeight()/2 : mItemList.get(i+1).getTop() - child.getMeasuredHeight()/2;

            child.layout(
                centerX - child.getMeasuredWidth()/2,
                centerY - child.getMeasuredHeight()/2,
                centerX + child.getMeasuredWidth()/2,
                centerY + child.getMeasuredHeight()/2
            );

            final int index = i;
            child.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) mOnItemClickListener.onItemClick(index);
                }
            });

        }

    }

    public interface OnItemClickListener {
        void onItemClick(int id);
    }
}

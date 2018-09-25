package ru.rewindforce.concerts.Views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ru.rewindforce.concerts.R;

public class FloatingMultiActionLayout extends ViewGroup{

    private static final String TAG = FloatingMultiActionLayout.class.getSimpleName();

    public static final int GRAVITY_START = 0, GRAVITY_END = 2;
    public static final long EXPAND_ANIMATION_DURATION = 160;

    private Context mContext;
    private int mGravity;
    private int mItemPadding;
    private int mTextSize, mTextPadding;

    private ArrayList<FloatingActionButton> mItemList;
    private ArrayList<TextView> mPromptList;
    private int mItemCount;
    private OnItemClickListener mOnItemClickListener;

    private boolean isExpanded = true;

    private ColorDrawable mBackground;
    //Buttons parameters:
    private int mButtonsColor;
    private int mElevation;

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
        mPromptList = new ArrayList<>();



        mGravity = GRAVITY_END;
        mItemPadding = MetricUtils.dpToPx(10);
        mTextPadding = MetricUtils.dpToPx(5);
        mTextSize = MetricUtils.dpToPx(12);


        if (attrs != null){
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FloatingActionLayout, defStyleAttr, 0);
            mGravity = a.getInt(R.styleable.FloatingActionLayout_ButtonGravity, GRAVITY_END);
            mItemPadding = a.getDimensionPixelSize(R.styleable.FloatingActionLayout_ItemPadding, mItemPadding);
            mTextPadding = a.getDimensionPixelSize(R.styleable.FloatingActionLayout_TextPadding, mTextPadding);
            mTextSize = a.getDimensionPixelSize(R.styleable.FloatingActionLayout_TextSize, mTextSize);
        }

        if (getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL){
            if (mGravity == GRAVITY_START) mGravity = GRAVITY_END;
            else mGravity = GRAVITY_START;
        }

        mBackground = new ColorDrawable(0xFF404040);
        mBackground.setAlpha(0);
        setBackground(mBackground);

        setExpanded(false);

    }

    public void setExpanded(boolean toExpand){
        isExpanded = toExpand;
        for (final View view: mItemList){
            if (mItemList.indexOf(view) != mItemCount-1) {
                View mainButton = mItemList.get(mItemCount - 1);
                if (isExpanded) {
                    view.animate()
                            .translationY(0)
                            .setDuration(EXPAND_ANIMATION_DURATION)
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
                            .setDuration(EXPAND_ANIMATION_DURATION)
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
        bgColorAnimator.setDuration(EXPAND_ANIMATION_DURATION);
        bgColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mBackground.setAlpha((int)animation.getAnimatedValue());
            }
        });
        bgColorAnimator.start();
    }

    public boolean isExpanded(){
        return isExpanded;
    }

    public int addItem(FloatingActionButton item, String text) {
        int id = mItemCount;
        mItemCount++;
        mItemList.add(item);
        TextView prompt = new TextView(mContext);
        prompt.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        prompt.setTextSize(mTextSize);
        prompt.setText(text);
        prompt.setTextColor(0xFFFFFFFF);
        prompt.setPadding(mTextPadding, mTextPadding, mTextPadding, mTextPadding);
        ShapeDrawable shapeDrawable = new ShapeDrawable();

        ShapeDrawable drawable = new ShapeDrawable();
        drawable.setShape(new RoundRectShape(new float[] {
                MetricUtils.dpToPx(5),
                MetricUtils.dpToPx(5),
                MetricUtils.dpToPx(5),
                MetricUtils.dpToPx(5)}, null, null));
        drawable.getPaint().setColor(0xFF404040);

        prompt.setBackground(new ShapeDrawable());
        mPromptList.add(prompt);
        addView(prompt);
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

    /**
     * @param elevation desired elevation in dp
     */
    public void setElevation(int elevation){
        if (mElevation == elevation) return;
        mElevation = elevation;
        requestLayout();
    }

    public FloatingActionButton getItem(int id) throws IndexOutOfBoundsException{
        if (id < 0 || id >= mItemCount) throw new IndexOutOfBoundsException("No item with provided Id found");
        return mItemList.get(id);
    }

    public String getItemPrompt(int id) throws IndexOutOfBoundsException{
        if (id < 0 || id >= mItemCount) throw new IndexOutOfBoundsException("No itemPrompt with provided Id found");
        return mPromptList.get(id).getText().toString();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
                View.resolveSize(0, widthMeasureSpec),
                View.resolveSize(0, heightMeasureSpec));
        for (int i=0; i < mItemCount; i++) {
            FloatingActionButton child = mItemList.get(i);
            child.setSize(i == mItemCount-1 ? FloatingActionButton.BUTTON_SIZE_DEFAULT : FloatingActionButton.BUTTON_SIZE_MINI);
            child.setButtonElevation(mElevation);
        }
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i=mItemCount-1; i>=0; i--){
            View child = mItemList.get(i);

            int centerX;
            if (mGravity == GRAVITY_START) centerX = getLeft() + getPaddingLeft() + mItemList.get(mItemCount-1).getMeasuredWidth()/2;
            else centerX = getRight() - getPaddingRight() - mItemList.get(mItemCount-1).getMeasuredWidth()/2;

            int centerY = i==mItemCount-1 ?
                    getBottom()-getPaddingBottom() - child.getMeasuredHeight()/2 :
                    mItemList.get(i+1).getTop() - child.getMeasuredHeight()/2 - mItemPadding;

            child.layout(
                centerX - child.getMeasuredWidth()/2,
                centerY - child.getMeasuredHeight()/2,
                centerX + child.getMeasuredWidth()/2,
                centerY + child.getMeasuredHeight()/2
            );

            mPromptList.get(i).layout(
                    mGravity == GRAVITY_START ? getPaddingLeft() + mItemList.get(mItemCount-1).getMeasuredWidth() : getPaddingLeft(),
                    child.getTop(),
                    mGravity == GRAVITY_START ? getRight() - getPaddingRight() : getRight() - getPaddingRight() - mItemList.get(mItemCount-1).getMeasuredWidth(),
                    child.getBottom()
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

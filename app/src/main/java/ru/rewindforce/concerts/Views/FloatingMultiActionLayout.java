package ru.rewindforce.concerts.Views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    private int mSelectedItem;

    private ArrayList<FloatingActionButton> mItemList;
    private ArrayList<TextView> mTextList;
    private ArrayList<ImageView> mIconList;
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
        setChildrenDrawingOrderEnabled(true);
        mContext = context;
        mItemList = new ArrayList<>();
        mTextList = new ArrayList<>();
        mIconList = new ArrayList<>();



        mGravity = GRAVITY_END;
        mItemPadding = MetricUtils.dpToPx(10);
        mTextPadding = MetricUtils.dpToPx(5);
        mTextSize = 12; //setting textSize in sp;


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

        mBackground = new ColorDrawable(0xFF000000);
        mBackground.setAlpha(204); // 80% translucency
        setBackground(mBackground);

        setExpanded(true);

        addItem(new FloatingActionButton(mContext), "default prompt");

    }

    public void setExpanded(boolean toExpand){
        if (isExpanded != toExpand) {
            isExpanded = toExpand;
            for (int i = 0; i < mItemCount; i++) {
                final View button = mItemList.get(i);
                final View text = mTextList.get(i);
                View mainButton = mItemList.get(0);
                if (isExpanded) {
                    if (mainButton != button)
                        button.animate()
                                .translationY(0)
                                .setDuration(EXPAND_ANIMATION_DURATION)
                                .withStartAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        button.setVisibility(VISIBLE);
                                    }
                                }).start();
                    text.animate()
                            .translationY(0)
                            .setDuration(EXPAND_ANIMATION_DURATION)
                            .alpha(1f)
                            .withStartAction(new Runnable() {
                                @Override
                                public void run() {
                                    text.setVisibility(VISIBLE);
                                }
                            }).start();
                } else {
                    if (mainButton != button)
                        button.animate()
                                .translationY(mainButton.getY() - button.getY() + mainButton.getMeasuredHeight() / 2 - button.getMeasuredHeight() / 2)
                                .setDuration(EXPAND_ANIMATION_DURATION)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        button.setVisibility(INVISIBLE);
                                    }
                                }).start();
                    text.animate()
                            .translationY(mainButton.getY() - text.getY() + mainButton.getMeasuredHeight() / 2 - text.getMeasuredHeight() / 2)
                            .setDuration(EXPAND_ANIMATION_DURATION)
                            .alpha(0f)
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    text.setVisibility(INVISIBLE);
                                }
                            }).start();
                }
            }
            int alpha = mBackground.getAlpha();
            ValueAnimator bgColorAnimator = ValueAnimator.ofInt(alpha, isExpanded ? 204 : 0);
            bgColorAnimator.setDuration(EXPAND_ANIMATION_DURATION);
            bgColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mBackground.setAlpha((int) animation.getAnimatedValue());
                }
            });
            bgColorAnimator.start();
        }
    }

    public void setSelectedItem(int id){
        if (mSelectedItem != id ){

            mSelectedItem = id;
            mItemList.get(0).setIcon(mItemList.get(id).getIcon());

        }
    }

    public boolean isExpanded(){
        return isExpanded;
    }

    public int addItem(FloatingActionButton item, String text) {
        int id = mItemCount;
        mItemCount++;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            item.setElevation(MetricUtils.dpToPx(6));
        }
        mItemList.add(item);
        TextView prompt = new TextView(mContext);
        prompt.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        prompt.setTextSize(mTextSize);
        prompt.setText(text);
        prompt.setTextColor(0xFFFFFFFF);
        prompt.setPadding(mTextPadding, mTextPadding, mTextPadding, mTextPadding);
        prompt.setGravity(mGravity == GRAVITY_START ? Gravity.START : Gravity.END);

        ShapeDrawable drawable = new ShapeDrawable();
        drawable.setShape(new RoundRectShape(new float[] {
                MetricUtils.dpToPx(5),
                MetricUtils.dpToPx(5),
                MetricUtils.dpToPx(5),
                MetricUtils.dpToPx(5),
                MetricUtils.dpToPx(5),
                MetricUtils.dpToPx(5),
                MetricUtils.dpToPx(5),
                MetricUtils.dpToPx(5)}, null, null));
        drawable.getPaint().setColor(0xFF404040);

        prompt.setBackground(drawable);
        mTextList.add(prompt);

        addView(prompt);
        addView(item);
        requestLayout();
        return id;
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mOnItemClickListener = listener;
    }


    /**
     * @param color desired buttons color
     */
    public void setButtonsColor(@ColorInt int color){
        if (mButtonsColor == color) return;
        mButtonsColor = color;
        requestLayout();
    }
    /**
     * @param elevation desired elevation in dp
     */
    public void setButtonsElevation(int elevation){
        if (mElevation == elevation) return;
        mElevation = elevation;
        requestLayout();
    }

    public int getItemCount(){
        return mItemCount;
    }

    public FloatingActionButton getItem(int id) throws IndexOutOfBoundsException{
        if (id < 0 || id >= mItemCount) throw new IndexOutOfBoundsException("No item with provided Id found");
        return mItemList.get(id);
    }

    public String getItemPrompt(int id) throws IndexOutOfBoundsException{
        if (id < 0 || id >= mItemCount) throw new IndexOutOfBoundsException("No itemPrompt with provided Id found");
        return mTextList.get(id).getText().toString();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
                View.resolveSize(0, widthMeasureSpec),
                View.resolveSize(0, heightMeasureSpec));
        for (int i=0; i < mItemCount; i++) {
            FloatingActionButton child = mItemList.get(i);
            child.setSize(i == 0 ? FloatingActionButton.SIZE_NORMAL : FloatingActionButton.SIZE_MINI);
            if (mButtonsColor != 0) child.setColor(mButtonsColor);
            if (mElevation != 0) child.setButtonElevation(mElevation);

        }
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int prevIndex = 0;
        for (int i=mItemCount-1; i>=0; i--){
            int index = i == mItemCount-1 ? 0 : i+1;

            View child = mItemList.get(index);

            int centerX;
            if (mGravity == GRAVITY_START) centerX = getLeft() + getPaddingLeft() + mItemList.get(0).getMeasuredWidth()/2;
            else centerX = getRight() - getPaddingRight() - mItemList.get(0).getMeasuredWidth()/2;

            int centerY = index==0 ?
                    getBottom()-getPaddingBottom() - child.getMeasuredHeight()/2 :
                    mItemList.get(prevIndex).getTop() - child.getMeasuredHeight()/2 - mItemPadding;

            child.layout(
                centerX - child.getMeasuredWidth()/2,
                centerY - child.getMeasuredHeight()/2,
                centerX + child.getMeasuredWidth()/2,
                centerY + child.getMeasuredHeight()/2
            );

            int promptLeft;
            int promptRight;
            int mainButtonWidth = mItemList.get(0).getMeasuredWidth();
            if (mGravity == GRAVITY_START) {
                promptLeft = (int) (getPaddingLeft() + mainButtonWidth + mainButtonWidth*0.1);
                promptRight = Math.min(
                        promptLeft + mTextList.get(index).getMeasuredWidth(),
                        getRight() - getPaddingRight());
            } else {
                promptRight = (int) (getRight() - getPaddingRight() - mainButtonWidth - mainButtonWidth*0.1);
                promptLeft = Math.max(
                        promptRight - mTextList.get(index).getMeasuredWidth(),
                        getPaddingLeft());
            }

            mTextList.get(index).layout(
                    promptLeft,
                    Math.max(centerY- mTextList.get(index).getMeasuredHeight()/2, child.getTop()),
                    promptRight,
                    Math.min(centerY+ mTextList.get(index).getMeasuredHeight()/2, child.getBottom())
            );

            if (index == 0){
                child.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setExpanded(!isExpanded);
                    }
                });
            } else {
                final int id = index;
                child.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) mOnItemClickListener.onItemClick(id-1);
                    }
                });
            }
            prevIndex = index;
        }
        if (!isExpanded) {
            View mainButton = mItemList.get(mItemCount - 1);
            for (int i = 0; i < mItemCount; i++) {
                if (i != mItemCount-1) {
                    mItemList.get(i).setTranslationY(mainButton.getY() - mItemList.get(i).getY() + mainButton.getMeasuredHeight() / 2 - mItemList.get(i).getMeasuredHeight() / 2);
                    mItemList.get(i).setVisibility(INVISIBLE);
                }

                if (i != mItemCount-1) mTextList.get(i).setTranslationY( mainButton.getY() - mTextList.get(i).getY() + mainButton.getMeasuredHeight()/2 - mTextList.get(i).getMeasuredHeight()/2);
                mTextList.get(i).setAlpha(0f);
            }
            mBackground.setAlpha(0);
        }

    }

    /**
     * Returns the index of the child to draw for this iteration. Override this
     * if you want to change the drawing order of children. By default, it
     * returns i.
     * <p>
     * NOTE: In order for this method to be called, you must enable child ordering
     * first by calling {@link #setChildrenDrawingOrderEnabled(boolean)}.
     *
     * @param childCount
     * @param i          The current iteration.
     * @return The index of the child to draw this iteration.
     * @see #setChildrenDrawingOrderEnabled(boolean)
     * @see #isChildrenDrawingOrderEnabled()
     */
    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        int result;
        if (i < childCount - 2) result = i+2;
        else result = (childCount-1) - i;
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!super.onTouchEvent(event)) setExpanded(false);
        return super.onTouchEvent(event);
    }

    public interface OnItemClickListener {
        void onItemClick(int id);
    }
}

package ru.rewindforce.concerts.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import androidx.annotation.ColorInt;
import ru.rewindforce.concerts.R;

public class FloatingMultiActionLayout extends ViewGroup{

    private static final String TAG = FloatingMultiActionLayout.class.getSimpleName();

    public static final int GRAVITY_START = 0, GRAVITY_END = 2;
    public static final long EXPAND_ANIMATION_DURATION = 160;
    public static final long SELECT_ANIMATION_DURATION = 160;

    private Context mContext;
    private int mGravity;
    private int mItemGap;
    private int mTextPadding;
    private int mSelectedItem;
    private float mTextSize;

    private ArrayList<FloatingActionButton> mItemList;
    private ArrayList<TextView> mTextList;
    private ArrayList<Drawable> mIconList;
    private int mItemCount;
    private OnItemClickListener mOnItemClickListener;

    private boolean isExpanded = false, isAnimating = false;

    private ColorDrawable mBackground;
    //Buttons parameters:
    private ColorStateList mButtonsColorStateList;
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
        mItemGap = MetricUtils.dpToPx(10);
        mTextPadding = MetricUtils.dpToPx(5);
        mTextSize = 12; //setting textSize in sp;

        if (attrs != null){
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FloatingActionLayout, defStyleAttr, 0);
            mGravity = a.getInt(R.styleable.FloatingActionLayout_ButtonGravity, GRAVITY_END);
            mItemGap = a.getDimensionPixelSize(R.styleable.FloatingActionLayout_ItemPadding, mItemGap);
            mTextPadding = a.getDimensionPixelSize(R.styleable.FloatingActionLayout_TextPadding, mTextPadding);
            mTextSize = a.getDimension(R.styleable.FloatingActionLayout_TextSize, mTextSize);
            setButtonsColor(a.getColor(R.styleable.FloatingActionLayout_ButtonColor, 0));
        }

        if (getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL){
            if (mGravity == GRAVITY_START) mGravity = GRAVITY_END;
            else mGravity = GRAVITY_START;
        }

        mBackground = new ColorDrawable(0xFF000000);
        mBackground.setAlpha(isExpanded ? 204 : 0); // 80% translucency

        setExpanded(false);

        setBackground(mBackground);

        addItem("default prompt");

    }

    public void setPrompt(String prompt){
        mTextList.get(0).setText(prompt);
        requestLayout();
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

    public boolean isExpanded(){
        return isExpanded;
    }

    public int addItem(String text){
        return addItem(null, text, null);
    }

    public int addItem(String text, Drawable icon) {
        return addItem(null, text, icon);
    }

    public int addItem(FloatingActionButton item, String text, Drawable icon) {
        if (item == null) item = new FloatingActionButton(mContext);
        int id = mItemCount;
        mItemCount++;
        if (id == 0) {
            MarginLayoutParams marginParams = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            marginParams.setMargins(0, 0, 0, MetricUtils.dpToPx(16));
            marginParams.setMarginEnd(MetricUtils.dpToPx(16));
            item.setLayoutParams(marginParams);
        }
        item.setImageDrawable(icon);
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
        mIconList.add(icon);
        addView(prompt);
        addView(item);
        requestLayout();
        return id;
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mOnItemClickListener = listener;
    }

    public void setSelectedItem(int id){
        if (mSelectedItem != id && !isAnimating){
            mSelectedItem = id;
            float currentRotation = mItemList.get(0).getRotation() % 360;
            float endValue = currentRotation == 0 ? mItemList.get(0).getRotation() + 360 : mItemList.get(0).getRotation() + 360 - currentRotation;
            final ValueAnimator valueAnimator = ValueAnimator.ofFloat(mItemList.get(0).getRotation(), endValue);
            valueAnimator.setDuration(SELECT_ANIMATION_DURATION);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float)animation.getAnimatedValue();
                    mItemList.get(0).setRotation(value);
                    if (mItemList.get(0).getRotation() % 360 > 180) mItemList.get(0).setImageDrawable(mIconList.get(mSelectedItem));
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    setExpanded(false);
                }
            });
            valueAnimator.start();
        }
    }

    /**
     * @param color desired buttons color
     */
    public void setButtonsColor(@ColorInt int color){
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_enabled}, // enabled
                new int[] {-android.R.attr.state_enabled}, // disabled
                new int[] {-android.R.attr.state_checked}, // unchecked
                new int[] { android.R.attr.state_pressed}  // pressed
        };
        int[] colors = new int[] {
                color,
                color,
                color,
                color
        };
        mButtonsColorStateList = new ColorStateList(states, colors);
        requestLayout();
    }

    /**
     * @param textSize размер текста (sp)
     */
    public void setTextSize(float textSize){
        if (mTextSize == textSize) return;
        mTextSize = textSize;
        requestLayout();
    }

    public void setItemGap(float itemGap){
        if (mItemGap == MetricUtils.dpToPx(itemGap)) return;
        mItemGap = MetricUtils.dpToPx(itemGap);
        requestLayout();
    }

    /**
     * Отступ текста от его фона
     * @param textPadding размер отступа (dp)
     */
    public void setTextPadding(int textPadding){
        if (mTextPadding == MetricUtils.dpToPx(textPadding)) return;
        mTextPadding = MetricUtils.dpToPx(textPadding);
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
            if (mButtonsColorStateList != null) {
                child.setBackgroundTintList(mButtonsColorStateList);
            }
        }
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int prevIndex = 0;
        int mainCenterX, mainCenterY;

        if (mGravity == GRAVITY_START) {
            mainCenterX = getLeft() + getPaddingLeft() + mItemList.get(0).getMeasuredWidth() / 2;
            mainCenterX += ((MarginLayoutParams) mItemList.get(0).getLayoutParams()).getMarginEnd();
        } else {
            mainCenterX = getRight() - getPaddingRight() - mItemList.get(0).getMeasuredWidth() / 2;
            mainCenterX -= ((MarginLayoutParams)mItemList.get(0).getLayoutParams()).getMarginEnd();
        }

        mainCenterY = getBottom() - getPaddingBottom() - mItemList.get(0).getMeasuredHeight()/2;

        for (int i=mItemCount-1; i>=0; i--){
            int index = i == mItemCount-1 ? 0 : i+1;

            View child = mItemList.get(index);

            if (index == 0){
                mainCenterY -= ((MarginLayoutParams)child.getLayoutParams()).bottomMargin;
            } else {
                mainCenterY = mItemList.get(prevIndex).getTop() - mItemGap - child.getMeasuredHeight()/2;
            }

            child.layout(
                    mainCenterX - child.getMeasuredWidth()/2,
                    mainCenterY - child.getMeasuredHeight()/2,
                    mainCenterX + child.getMeasuredWidth()/2,
                    mainCenterY + child.getMeasuredHeight()/2
            );

            int promptLeft;
            int promptRight;
            int mainButtonWidth = mItemList.get(0).getMeasuredWidth();
            if (mGravity == GRAVITY_START) {
                promptLeft = (int) (mainCenterX + mainButtonWidth/2 + MetricUtils.dpToPx(10));
                promptRight = Math.min(
                        promptLeft + mTextList.get(index).getMeasuredWidth(),
                        getRight() - getPaddingRight());
            } else {
                promptRight = (int) (mainCenterX - mainButtonWidth/2 - MetricUtils.dpToPx(10));
                promptLeft = Math.max(
                        promptRight - mTextList.get(index).getMeasuredWidth(),
                        getPaddingLeft());
            }

            mTextList.get(index).layout(
                    promptLeft,
                    Math.max(mainCenterY- mTextList.get(index).getMeasuredHeight()/2, child.getTop()),
                    promptRight,
                    Math.min(mainCenterY+ mTextList.get(index).getMeasuredHeight()/2, child.getBottom())
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
                        if (mOnItemClickListener != null) mOnItemClickListener.onItemClick(id);
                    }
                });
            }
            prevIndex = index;
        }
        if (!isExpanded) {
            View mainButton = mItemList.get(0);
            for (int i = 0; i < mItemCount; i++) {
                if (i != 0) {
                    float value = mainButton.getY() - mItemList.get(i).getY() + (mainButton.getMeasuredHeight()- mItemList.get(i).getMeasuredHeight())/2;
                    if (value != 0) mItemList.get(i).setTranslationY(value);
                    mItemList.get(i).setVisibility(INVISIBLE);
                }
                float textValue = mainButton.getY() - mTextList.get(i).getY() + mainButton.getMeasuredHeight()/2 - mTextList.get(i).getMeasuredHeight()/2;
                if (textValue != 0) mTextList.get(i).setTranslationY(textValue);
                mTextList.get(i).setAlpha(0f);
            }
            mBackground.setAlpha(0);
        }

    }
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

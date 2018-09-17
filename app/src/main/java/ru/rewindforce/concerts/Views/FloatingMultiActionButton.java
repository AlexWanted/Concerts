package ru.rewindforce.concerts.Views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;



import java.util.ArrayList;
import java.util.NoSuchElementException;

import ru.rewindforce.concerts.R;

public class FloatingMultiActionButton extends View {

    private static final String TAG = FloatingMultiActionButton.class.getSimpleName();
    public static final int SIZE_NOT_SPECIFIED = -1;
    private static final int GRAVITY_START = 0, GRAVITY_END = 1;
    private static final long EXPAND_ANIM_DURATION = 100;

    private Context mContext;

    private int width, height;
    ArrayList<Item> items;
    private int mGravity;
    private float mShadowPadding;
    private float mButtonSize;
    private float mItemTextSize;
    private float mItemTextPadding;
    private float mIconPadding;
    private float mPromptTextSize;
    private float mItemButtonSize;
    private String mPrompt;
    private int mItemTextColor;
    private OnItemClickListener onItemClickListener;

    private float expandAnimatedValue = 0f;
    private float selectAnimatedValue = 0f;

    private Rect mViewBounds;
    private Rect mMainButtonBounds;
    private boolean isExpanded;
    private boolean isAnimating;
    private Rect mPromptTextRect;
    private Rect mItemTextRect;
    private Rect mItemTempBounds;
    Path itemPromptPath, mCirlceClipPath;

    private int mSelectedItem = 0;
    private int mPrevSelectedItem = -1;

    /*private int mButtonColor;
    private int mButtonColorRes;*/

    private int mButtonColor;
    private Paint mButtonPaint, mTextPaint, mTextBackgroundPaint, bgPaint;


    public FloatingMultiActionButton(Context context) {
        this(context, null);
    }

    public FloatingMultiActionButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingMultiActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;
        items = new ArrayList<>();

        mViewBounds = new Rect();
        mMainButtonBounds = new Rect();
        mItemTempBounds = new Rect();
        mPromptTextRect = new Rect();
        mItemTextRect = new Rect();

        mButtonPaint = new Paint();
        mTextBackgroundPaint = new Paint();
        mTextPaint = new Paint();
        bgPaint = new Paint();
        bgPaint.setColor(Color.BLACK);

        itemPromptPath = new Path();
        mCirlceClipPath = new Path();

        setLayerType(LAYER_TYPE_SOFTWARE, null);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FloatingMultiActionButton, defStyleAttr, 0);
            mButtonSize = a.getDimension(R.styleable.FloatingMultiActionButton_fmabSize,  SIZE_NOT_SPECIFIED);

            mButtonColor = a.getColor(R.styleable.FloatingMultiActionButton_fmabColor, 0xFFFFFFF);
            mShadowPadding = a.getDimension(R.styleable.FloatingMultiActionButton_fmabElevation, 0);
            mPrompt = a.getString(R.styleable.FloatingMultiActionButton_fmabPrompt);
            mPromptTextSize = a.getDimension(R.styleable.FloatingMultiActionButton_fmabPromptTextSize, 80);
            mItemTextSize = a.getDimension(R.styleable.FloatingMultiActionButton_fmabItemTextSize, 60);
            mItemTextPadding = a.getDimension(R.styleable.FloatingMultiActionButton_fmabItemTextPadding, 20);
            mItemTextColor = a.getColor(R.styleable.FloatingMultiActionButton_fmabItemTextColor, Color.WHITE);
            mGravity = a.getInt(R.styleable.FloatingMultiActionButton_fmabGravity, GRAVITY_END);
            mIconPadding = a.getDimension(R.styleable.FloatingMultiActionButton_fmabIconPadding, 25);
            a.recycle();

        }

        mButtonPaint.setColor(mButtonColor);
        mButtonPaint.setStyle(Paint.Style.FILL);
        mButtonPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);

        mTextBackgroundPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        //mTextBackgroundPaint.setColor();

        if (mButtonSize == SIZE_NOT_SPECIFIED){
            mButtonSize = getResources().getDisplayMetrics().widthPixels/5.25f;
        }
        mItemButtonSize = (float) (mButtonSize *0.8);

        if (getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL){
            if (mGravity == GRAVITY_START) mGravity = GRAVITY_END;
            else mGravity = GRAVITY_START;
        }
        mTextPaint.setColor(mItemTextColor);
        mTextPaint.setTextSize(mItemTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);

    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (isAnimating || isExpanded) {
            bgPaint.setAlpha((int) (255*expandAnimatedValue*0.5));
            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), bgPaint);

            for (Item item: items) {

                /*mTextPaint.getTextBounds(item.getText(), 0, item.getText().length(), mItemTextRect);

                int width = (int) (mItemTextRect.width() + mItemTextPadding*2);
                int height = (int) (mItemTextRect.height() + mItemTextPadding*2);

                if (mGravity == GRAVITY_START) {
                    mItemTextRect.left = mMainButtonBounds.right;
                    mItemTextRect.right = mItemTextRect.left + width;
                } else {
                    mItemTextRect.right = mMainButtonBounds.left;
                    mItemTextRect.left = mItemTextRect.right - width;
                }

                mItemTextRect.top = item.getCenterY()-height/2;
                mItemTextRect.bottom = item.getCenterY()+height/2;
                mTextBackgroundPaint.setAlpha((int)(255*expandAnimatedValue));

                int radius = (int) (mItemTextSize * 0.25);
                itemPromptPath.reset();
                itemPromptPath.moveTo(mItemTextRect.right, mItemTextRect.bottom-radius);
                itemPromptPath.quadTo(mItemTextRect.right, mItemTextRect.bottom, mItemTextRect.right-radius, mItemTextRect.bottom);
                itemPromptPath.lineTo(mItemTextRect.left+radius, mItemTextRect.bottom);
                itemPromptPath.quadTo(mItemTextRect.left, mItemTextRect.bottom, mItemTextRect.left, mItemTextRect.bottom-radius);
                itemPromptPath.lineTo(mItemTextRect.left, mItemTextRect.top+radius);
                itemPromptPath.quadTo(mItemTextRect.left, mItemTextRect.top, mItemTextRect.left+radius, mItemTextRect.top);
                itemPromptPath.lineTo(mItemTextRect.right-radius, mItemTextRect.top);
                itemPromptPath.quadTo(mItemTextRect.right, mItemTextRect.top, mItemTextRect.right, mItemTextRect.top+radius);
                itemPromptPath.lineTo(mItemTextRect.right, mItemTextRect.bottom-radius);
                canvas.drawPath(itemPromptPath, mTextBackgroundPaint);*/

                mTextPaint.setAlpha((int) (255*expandAnimatedValue));
                canvas.drawText(item.getText(), mItemTextRect.centerX(), mItemTextRect.centerY() + mItemTextSize/3, mTextPaint);

                canvas.drawCircle(item.getCenterX(), item.getCenterY(), mItemButtonSize/2, mButtonPaint);

                item.getIcon().setBounds(
                        (int) (item.getCenterX()-mItemButtonSize/2 + mIconPadding),
                        (int) (item.getCenterY()-mItemButtonSize/2 + mIconPadding),
                        (int) (item.getCenterX()+mItemButtonSize/2 - mIconPadding ),
                        (int) (item.getCenterY()+mItemButtonSize/2 - mIconPadding)
                );
                item.getIcon().draw(canvas);
            }
        } else {
            mViewBounds.top = (int) (mViewBounds.bottom-mButtonSize);
        }

        canvas.drawCircle(mMainButtonBounds.centerX(), mMainButtonBounds.centerY(), mButtonSize/2, mButtonPaint);

        /*items.get(mSelectedItem).getIcon().setBounds(
                (int) (mMainButtonBounds.centerX()-(mMainButtonBounds.width()   2 * Math.abs(0.5-selectAnimatedValue))/2 + mIconPadding),
                (int) (mMainButtonBounds.centerY()-(mMainButtonBounds.height()  2 * Math.abs(0.5-selectAnimatedValue))/2 + mIconPadding),
                (int) (mMainButtonBounds.centerX()+(mMainButtonBounds.width()   2 * Math.abs(0.5-selectAnimatedValue))/2 - mIconPadding),
                (int) (mMainButtonBounds.centerY()+(mMainButtonBounds.height()  2 * Math.abs(0.5-selectAnimatedValue))/2 - mIconPadding));
        items.get(mSelectedItem).getIcon().draw(canvas);*/

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final float preferedSize = getResources().getDisplayMetrics().widthPixels/5f;

        int measuredWidth = (int) (preferedSize+getPaddingLeft()+getPaddingRight()+ mShadowPadding *2);
        int measuredHeight = (int) (preferedSize*(getItemCount()+1)+getPaddingBottom()+getPaddingTop()+ mShadowPadding *2);

        setMeasuredDimension(
                getMeasuredDimension(measuredWidth, widthMeasureSpec),
                getMeasuredDimension(measuredHeight, heightMeasureSpec));
    }


    /**
     * This is called during layout when the size of this view has changed. If
     * you were just added to the view hierarchy, you're called with the old
     * values of 0.
     *
     * @param w    Current width of this view.
     * @param h    Current height of this view.
     * @param oldw Old width of this view.
     * @param oldh Old height of this view.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;

        mViewBounds.left = (int) (getPaddingLeft() + mShadowPadding);
        mViewBounds.top = (int) (getPaddingTop() + mShadowPadding);
        mViewBounds.right = (int) (width - getPaddingRight() - mShadowPadding);
        mViewBounds.bottom = (int) (height - getPaddingBottom() - mShadowPadding);

        if (mGravity == GRAVITY_START) {
            mViewBounds.right = (int) (mViewBounds.left + mButtonSize);
        } else {
            mViewBounds.left = (int) (mViewBounds.right - mButtonSize);
        }

        mMainButtonBounds.left = (int) (mViewBounds.right-mButtonSize);
        mMainButtonBounds.top = (int) (mViewBounds.bottom - getPaddingBottom() - mButtonSize );
        mMainButtonBounds.right = (int) (mMainButtonBounds.left+mButtonSize);
        mMainButtonBounds.bottom = (int) (mMainButtonBounds.top + mButtonSize);

        for (Item item: items){
            int centerY = (int) (mMainButtonBounds.centerY() - ((mButtonSize * (items.size()-items.indexOf(item))) + (mButtonSize - mItemButtonSize)/2 ));
            item.setCenterX(mMainButtonBounds.centerX());
            item.setExpandedY(centerY);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float mX = event.getX()+getPaddingLeft();
        float mY = event.getY()+getPaddingTop();
        if (mViewBounds.contains((int)mX, (int) mY)) {
            switchOut : switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    if ( mMainButtonBounds.contains((int) mX, (int) mY)){
                        if (!isExpanded)performClick();
                        setExpanded(!isExpanded);
                        break;
                    } else {
                        for (Item item : items) {
                            int distance = (int) Math.sqrt( Math.abs(item.centerX - mX) * Math.abs(item.centerX - mX) - Math.abs(item.centerY - mY) * Math.abs(item.centerY - mY) );
                            if ( distance <= mItemButtonSize/2) {
                                if (onItemClickListener != null) {
                                    if (mSelectedItem != items.indexOf(item)) {
                                        onItemClickListener.onItemClick(items.indexOf(item));
                                        setSelectedItem(items.indexOf(item));
                                    }

                                }
                                break switchOut;
                            }
                        }
                    }
                    break;
                }
                case MotionEvent.ACTION_CANCEL: {
                    break;
                }
            }
            return true;
        }
        setExpanded(false);
        return false;
    }

    public void setSelectedItem(final int id) throws NoSuchElementException{
        if (id != mSelectedItem) {
            if (id >= 0 && id < items.size()) {
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
                valueAnimator.setDuration(150);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        selectAnimatedValue = (float) animation.getAnimatedValue();
                        if (selectAnimatedValue >= 0.5f) mSelectedItem = id;
                        invalidate();
                    }
                });
                valueAnimator.start();
                setExpanded(false);
                return;
            }
            throw new NoSuchElementException("No items with provided Id were found");
        }
    }

    public int addItem(Item item) {
        int id = items.size();

        items.add(item);
        invalidate();
        requestLayout();
        return id;
    }

    public void setExpanded(boolean toExpand){
        if (isExpanded != toExpand) {
            isAnimating = true;
            isExpanded = toExpand;
            float endValue = toExpand ? 1f : 0f;
            PropertyValuesHolder holders[] = new PropertyValuesHolder[items.size()+1];

            for (int i=0; i<items.size(); i++){
                int endCenterY = toExpand ? items.get(i).expandedY : mMainButtonBounds.centerY();
                holders[i] = PropertyValuesHolder.ofInt(String.valueOf(i), items.get(i).getCenterY(), endCenterY);
            }
            holders[items.size()] = PropertyValuesHolder.ofFloat("expandAnimatedValue", expandAnimatedValue, endValue);
            final ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(holders);
            animator.setDuration(EXPAND_ANIM_DURATION);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    expandAnimatedValue = (float) animation.getAnimatedValue("expandAnimatedValue");
                    for (int i=0; i<items.size(); i++){
                        items.get(i).setCenterY((int) animation.getAnimatedValue(String.valueOf(i)));
                    }
                    invalidate(mViewBounds);
                }
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    isAnimating = false;
                }
            });
            animator.start();
        }
    }

    public void setPrompt(String prompt){
        if (mPrompt.equals(prompt)) return;
        mPrompt = prompt;
        invalidate();
    }

    public int getItemCount(){
        return items.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int id);
    }

    /**
     * Возваращает размер в зависимости от {@param measureSpec}.mode при возможности быть больше,
     * при отстутвии ограничений остаётся с желаемым размером
     * @param size желаемый размер
     * @param measureSpec ограничения layout'a
     * @return конечный размер
     */
    private int getMeasuredDimension(int size, int measureSpec){

        int result = size;

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode){
            case MeasureSpec.UNSPECIFIED: {
                result = size;
                break;
            }
            case MeasureSpec.AT_MOST: {
                result = Math.min(specSize,size);
                break;
            }
            case MeasureSpec.EXACTLY: {
                result = Math.max(specSize,size);
                break;
            }
        }
        return result;
    }

    public static class Item{

        private static final String TAG = Item.class.getSimpleName();
        private String mPrompt;
        private Drawable mIcon;
        private int centerY, centerX, expandedY;

        public Item(){
            mPrompt = "Default prompt";
            mIcon = new DrawableContainer();
        }

        /*public int getId() {
            return mId;
        }*/

        public String getText() {
            return mPrompt;
        }

        public Item setPrompt(String name) {
            if (mPrompt != null && mPrompt.equals(name)) return this;
            this.mPrompt = name;
            return this;
        }

        public Drawable getIcon() {
            return mIcon;
        }

        public Item setIcon(Drawable icon) {
            if (mIcon == icon) return this;
            this.mIcon = icon;
            return this;
        }

        private int getCenterX() {
            return centerX;
        }

        private void setCenterX(int x) {
            centerX = x;
        }

        private int getCenterY() {
            return centerY;
        }

        private void setCenterY(int y) {
            centerY = y;
        }

        private int getExpandedY() {
            return expandedY;
        }

        private void setExpandedY(int y) {
            expandedY = y;
        }

    }

}

package ru.rewindforce.concerts.Views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


import java.util.ArrayList;

import ru.rewindforce.concerts.R;

public class FloatingMultiActionButton extends View {

    private static final String TAG = FloatingMultiActionButton.class.getSimpleName();
    private static final long EXPAND_ANIM_DURATION = 100;

    public static final int SIZE_NOT_SPECIFIED = -1;



    private Context mContext;
    ArrayList<Item> items;
    private float mButtonSize;
    private float mSecButtonsSize;
    private String mPrompt;
    private OnItemClickListener onItemClickListener;

    private float expandAnimatedValue = 0f;

    private Rect mViewBounds;
    private Rect mMainButtonBounds;
    private boolean isExpanded;
    private boolean isAnimating;

    /*private int mButtonColor;
    private int mButtonColorRes;*/

    private Drawable mButtonBackground;
    private Paint mButtonPaint;

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

        mButtonPaint = new Paint();
        mButtonPaint.setColor(Color.BLACK);
        mButtonPaint.setStyle(Paint.Style.FILL);
        mButtonPaint.setAntiAlias(true);


        addItem(new Item(0).setName("Иду"));
        addItem(new Item(2).setName("Не иду"));
        addItem(new Item(1).setName("Хочу пойти"));
        addItem(new Item(4).setName("Возможно иду"));

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FloatingMultiActionButton, defStyleAttr, 0);
            mButtonSize = a.getDimension(R.styleable.FloatingMultiActionButton_size,  SIZE_NOT_SPECIFIED);
            if (mButtonSize == SIZE_NOT_SPECIFIED){
                mButtonSize = getResources().getDisplayMetrics().widthPixels/5.25f;
                mSecButtonsSize = (float) (mButtonSize *0.8);
            }
            mButtonBackground = a.getDrawable(R.styleable.FloatingMultiActionButton_buttonBackground);
            if (mButtonBackground instanceof ColorDrawable){
                mButtonPaint.setColor(((ColorDrawable)mButtonBackground).getColor());
            }
            a.recycle();
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.getClipBounds(mViewBounds);
        mViewBounds.left += getPaddingLeft();
        mViewBounds.top += getPaddingTop();
        mViewBounds.right -= getPaddingRight();
        mViewBounds.bottom -= getPaddingBottom();

        mMainButtonBounds.left = (int) (mViewBounds.centerX()-mButtonSize/2);
        mMainButtonBounds.top = (int) (mViewBounds.centerY()+(getItemCount()*mButtonSize)/2-mButtonSize/2);
        mMainButtonBounds.right = (int) (mViewBounds.centerX()+mButtonSize/2);
        mMainButtonBounds.bottom = (int) (mMainButtonBounds.top+ mButtonSize);

        mButtonBackground.setBounds(mMainButtonBounds);

        if (isAnimating || isExpanded) {
            for (int i = 0; i < items.size(); i++) {
                int itemPadding = (int) (mButtonSize - mSecButtonsSize);
                int centerX = mMainButtonBounds.centerX();
                int centerY = (int) (mMainButtonBounds.centerY() - ((mButtonSize * (i + 1)) + itemPadding / 2) * expandAnimatedValue);
                items.get(i).setBounds(
                        (int) (centerX - mSecButtonsSize / 2),
                        (int) (centerY - mSecButtonsSize / 2),
                        (int) (centerX + mSecButtonsSize / 2),
                        (int) (centerY + mSecButtonsSize / 2));

                if (mButtonBackground instanceof ColorDrawable) {
                    canvas.drawCircle(
                            items.get(i).getBounds().centerX(),
                            items.get(i).getBounds().centerY(),
                            mSecButtonsSize / 2,
                            mButtonPaint);
                } else {
                    mButtonBackground.setBounds(items.get(i).getBounds());
                    mButtonBackground.draw(canvas);
                }
            }
        }
        if (mButtonBackground instanceof ColorDrawable){
            canvas.drawCircle(
                    mMainButtonBounds.centerX(),
                    mMainButtonBounds.centerY(),
                    mButtonSize/2,
                    mButtonPaint);
        } else {
            mButtonBackground.draw(canvas);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final float preferedSize = getResources().getDisplayMetrics().widthPixels/5f;

        int measuredWidth = (int)preferedSize+getPaddingLeft()+getPaddingRight();
        int measuredHeight = (int) (preferedSize*(getItemCount()+1)+getPaddingBottom()+getPaddingTop());

        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    public void setExpanded(boolean toExpand){
        if (isExpanded != toExpand) {
            isAnimating = true;
            isExpanded = toExpand;
            float endValue = toExpand ? 1f : 0f;
            ValueAnimator animator = ValueAnimator.ofFloat(expandAnimatedValue, endValue);
            animator.setDuration(EXPAND_ANIM_DURATION);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    expandAnimatedValue = (float) animation.getAnimatedValue();
                    invalidate();
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

    public FloatingMultiActionButton addItem(Item item) throws IllegalArgumentException{
        for (Item tempItem: items){
            if (tempItem.mId == item.mId) {
                throw new IllegalArgumentException("Inserting item with Id that is already in the list");
            }
        }
        items.add(item);
        return this;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float mX = event.getX()+getPaddingLeft();
        float mY = event.getY()+getPaddingTop();
        if (mViewBounds.contains((int)mX, (int) mY)) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    if ( mMainButtonBounds.contains((int) mX, (int) mY)){
                        //Log.d(TAG, "item clicked (main button)");
                        setExpanded(!isExpanded);
                    } else {
                        for (Item item : items) {
                            if (item.getBounds().contains((int) mX, (int) mY)) {
                                if (onItemClickListener != null) onItemClickListener.onItemClick(item.getId());
                                break;
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
        return super.onTouchEvent(event);
    }

    public int getItemCount(){
        return items.size();
    }

    public void setButtonBackground(Drawable drawable){
      if (mButtonBackground == drawable) return;
      mButtonBackground = drawable;
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int id);
    }

    public static class Item{

        private static final String TAG = Item.class.getSimpleName();
        private final int mId;
        private String mName;
        private Drawable mIcon, mBackground;
        private Rect bounds;

        public Item(int id){
            mId = id;
            bounds = new Rect();
            mName = "Default name";
        }

        public int getId() {
            return mId;
        }

        public String getName() {
            return mName;
        }

        public Item setName(String name) {
            if (mName != null && mName.equals(name)) return this;
            this.mName = name;
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

        public Drawable getBackground() {
            return mBackground;
        }

        public Item setBackground(Drawable background) {
            if (mBackground != null && mBackground == background) return this;
            mBackground = background;
            return this;
        }

        private Rect getBounds() {
            return bounds;
        }

        private void setBounds(int left, int top, int right, int bottom) {
            this.bounds.left = left;
            this.bounds.top = top;
            this.bounds.right = right;
            this.bounds.bottom = bottom;
        }
    }
}

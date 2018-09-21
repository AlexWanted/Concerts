package ru.rewindforce.concerts.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;


import ru.rewindforce.concerts.R;

public class FloatingActionButton extends View {

    private static final String TAG = FloatingActionButton.class.getSimpleName();
    public static final int SIZE_NOT_SPECIFIED = -1;

    private Context mContext;

    private String mPrompt;
    private int mButtonColor = 0xFF4DD0E1;
    private int mTextColor = Color.WHITE;
    private float mIconPadding = 20;
    private float mTextSize = 60;
    private float mElevation;
    private float mButtonSize;
    private Drawable mIcon;

    private Rect mViewBounds;
    private Rect mButtonBounds;
    private Rect mPromptRect;


    Path itemPromptPath, mCirlceClipPath;

    private Paint mButtonPaint, mTextPaint, mTextBackgroundPaint;


    public FloatingActionButton(Context context) {
        this(context, null);
    }

    public FloatingActionButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        mViewBounds = new Rect();
        mButtonBounds = new Rect();
        mPromptRect = new Rect();

        mButtonPaint = new Paint();
        mTextPaint = new Paint();
        mTextBackgroundPaint = new Paint();

        itemPromptPath = new Path();
        mCirlceClipPath = new Path();

        setLayerType(LAYER_TYPE_SOFTWARE, null);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FloatingActionButton, defStyleAttr, 0);
            mIconPadding = a.getDimension(R.styleable.FloatingActionButton_IconPadding, 25);

            mButtonSize = a.getDimensionPixelSize(R.styleable.FloatingActionButton_ButtonSize,  SIZE_NOT_SPECIFIED);
            mButtonColor = a.getColor(R.styleable.FloatingActionButton_ButtonColor, 0xFF4DD0E1);
            mElevation = a.getDimensionPixelSize(R.styleable.FloatingActionButton_ButtonElevation, 30);
            mPrompt = a.getString(R.styleable.FloatingActionButton_Prompt);
            mTextSize = a.getDimensionPixelSize(R.styleable.FloatingActionButton_TextSize, 60);
            mTextColor = a.getColor(R.styleable.FloatingActionButton_TextColor, Color.WHITE);
            a.recycle();

        }
        setSize(mButtonSize);
        if (mPrompt == null) mPrompt = "";

        setColor(mButtonColor);
        mButtonPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mButtonPaint.setShadowLayer(mElevation, 0, mElevation/2, 0x40000000);

        mTextBackgroundPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mTextBackgroundPaint.setColor(0xDADADA);

        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawCircle(mButtonBounds.centerX(), mButtonBounds.centerY(), mButtonSize/2, mButtonPaint);
        if (mIcon != null) {
            mIcon.setBounds(
                    (int) (mButtonBounds.centerX() - mButtonSize/2 + mIconPadding),
                    (int) (mButtonBounds.centerX() - mButtonSize/2 + mIconPadding),
                    (int) (mButtonBounds.centerX() + mButtonSize/2 - mIconPadding),
                    (int) (mButtonBounds.centerX() + mButtonSize/2 - mIconPadding)
            );
            mIcon.draw(canvas);
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final float preferedSize;
        if (mButtonSize != SIZE_NOT_SPECIFIED) preferedSize = mButtonSize;
        else preferedSize = getResources().getDisplayMetrics().widthPixels/5.25f;
        int measuredWidth = (int) (preferedSize + getPaddingLeft() + getPaddingRight() + preferedSize*0.025);
        int measuredHeight = (int) (preferedSize + getPaddingBottom() + getPaddingTop() + preferedSize*0.025);

        setMeasuredDimension(
                resolveSize(measuredWidth, widthMeasureSpec),
                resolveSize(measuredHeight, heightMeasureSpec)
        );
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
        mViewBounds.left = getPaddingLeft();
        mViewBounds.top = getPaddingTop();
        mViewBounds.right = w - getPaddingRight();
        mViewBounds.bottom = h - getPaddingBottom();

        mButtonBounds.left = (int) (mViewBounds.right-mButtonSize);
        mButtonBounds.top = (int) (mViewBounds.bottom  - mButtonSize );
        mButtonBounds.right = (int) (mButtonBounds.left+mButtonSize);
        mButtonBounds.bottom = (int) (mButtonBounds.top + mButtonSize);

    }


    public FloatingActionButton setPrompt(String text){
        if (mPrompt != null && mPrompt.equals(text)) return this;
        mPrompt = text;
        return this;
    }

    public FloatingActionButton setSize(float size){
        if (size == SIZE_NOT_SPECIFIED){
            mButtonSize = (float) (getResources().getDisplayMetrics().widthPixels/5.25);
        } else {
            mButtonSize = size;
        }
        requestLayout();
        return this;
    }

    public FloatingActionButton setColor(@ColorInt int color){
        mButtonColor = color;
        mButtonPaint.setColor(color);
        invalidate();
        return this;
    }

    public FloatingActionButton setButtonElevation(float elevation){
        mElevation = elevation;
        mButtonPaint.setShadowLayer(mElevation, 0, mElevation/2, 0x40000000);
        invalidate();
        return this;
    }

}

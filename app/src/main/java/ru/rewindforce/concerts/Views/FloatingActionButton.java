package ru.rewindforce.concerts.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;


import ru.rewindforce.concerts.R;

public class FloatingActionButton extends View {

    private static final String TAG = FloatingActionButton.class.getSimpleName();

    public static final int SIZE_NORMAL = 56;
    public static final int  SIZE_MINI = 40;


    private Context mContext;
    private int mButtonColor = 0xFF4DD0E1;
    private int mIconPadding;
    private int mElevation;
    private int mButtonSize;
    private Drawable mIcon;

    private Rect mViewBounds;
    private Rect mButtonBounds;

    private Paint mButtonPaint;


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
        mButtonPaint = new Paint();

        setLayerType(LAYER_TYPE_SOFTWARE, null);

        mIconPadding = MetricUtils.dpToPx(8);
        mButtonSize = SIZE_NORMAL;
        mElevation = MetricUtils.dpToPx(6);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FloatingActionLayout, defStyleAttr, 0);
            mIconPadding = a.getDimensionPixelSize(R.styleable.FloatingActionLayout_IconPadding, mIconPadding);
            mButtonSize = a.getDimensionPixelSize(R.styleable.FloatingActionLayout_ButtonSize, SIZE_NORMAL);
            mButtonColor = a.getColor(R.styleable.FloatingActionLayout_ButtonColor, 0xFF4DD0E1);
            mElevation = a.getDimensionPixelSize(R.styleable.FloatingActionLayout_ButtonElevation, mElevation);
            a.recycle();

        }
        setSize(mButtonSize);
        setColor(mButtonColor);
        mButtonPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mButtonPaint.setShadowLayer(mElevation/2, 0, mElevation/4, 0x40000000);

    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawCircle(mButtonBounds.centerX(), mButtonBounds.centerY(), mButtonSize/2, mButtonPaint);
        if (mIcon != null) {
            mIcon.setBounds(
                    mButtonBounds.centerX() - mButtonSize/2 + mIconPadding,
                    mButtonBounds.centerX() - mButtonSize/2 + mIconPadding,
                    mButtonBounds.centerX() + mButtonSize/2 - mIconPadding,
                    mButtonBounds.centerX() + mButtonSize/2 - mIconPadding
            );
            mIcon.draw(canvas);
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final float preferedSize;
        preferedSize = mButtonSize;
        int measuredWidth = (int) (preferedSize + getPaddingLeft() + getPaddingRight() + preferedSize*0.025 + mElevation);
        int measuredHeight = (int) (preferedSize + getPaddingBottom() + getPaddingTop() + preferedSize*0.025 + mElevation);

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
        mButtonBounds.left = getPaddingLeft();
        mButtonBounds.top = getPaddingTop();
        mButtonBounds.right = w - getPaddingRight();
        mButtonBounds.bottom = h - getPaddingBottom();

    }

    /**
     * @param size desired size in dp
     * @return
     */
    public FloatingActionButton setSize(int size){
        if (mButtonSize == MetricUtils.dpToPx(size)) return this;
        mButtonSize = MetricUtils.dpToPx(size);
        requestLayout();
        return this;
    }

    public int getSize(){
        return mButtonSize;
    }

    public FloatingActionButton setColor(@ColorInt int color){
        mButtonColor = color;
        mButtonPaint.setColor(color);
        invalidate();
        return this;
    }

    public FloatingActionButton setIcon(Drawable icon){
        if (icon == mIcon) return this;
        if (icon != null) {
            mIcon = icon;
            invalidate();
        }
        return this;
    }

    /**
     * @param padding desired padding in dp
     * @return
     */
    public void setIconPadding(int padding){
        if (MetricUtils.dpToPx(padding) == mIconPadding ) return;
        mIconPadding = MetricUtils.dpToPx(padding);
        invalidate();
    }

    /**
     * @param elevation desired elevation in dp
     * @return
     */
    public FloatingActionButton setButtonElevation(int elevation){
        mElevation = MetricUtils.dpToPx(elevation);
        mButtonPaint.setShadowLayer(mElevation/2, 0, mElevation/4, 0x40000000);
        requestLayout();
        return this;
    }

    public Drawable getIcon(){
        return mIcon;
    }

}

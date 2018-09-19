package ru.rewindforce.concerts.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;



import java.util.ArrayList;

import ru.rewindforce.concerts.R;

public class FloatingActionButton extends View {

    private static final String TAG = FloatingActionButton.class.getSimpleName();
    public static final int SIZE_NOT_SPECIFIED = -1;
    private static final int GRAVITY_START = 0, GRAVITY_END = 1;
    //private static final long EXPAND_ANIM_DURATION = 100;

    private Context mContext;

    private int width, height;
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
    private Rect mButtonBounds;
    private Rect mPromptRect;

    private Drawable mIcon;

    Path itemPromptPath, mCirlceClipPath;

    private int mButtonColor;
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
        mTextBackgroundPaint = new Paint();
        mTextPaint = new Paint();


        itemPromptPath = new Path();
        mCirlceClipPath = new Path();

        setLayerType(LAYER_TYPE_SOFTWARE, null);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FloatingActionButton, defStyleAttr, 0);
            mButtonSize = a.getDimension(R.styleable.FloatingActionButton_fmabSize,  SIZE_NOT_SPECIFIED);

            mButtonColor = a.getColor(R.styleable.FloatingActionButton_fmabColor, 0xFFFFFFF);
            mShadowPadding = a.getDimension(R.styleable.FloatingActionButton_fmabElevation, 0);
            mPrompt = a.getString(R.styleable.FloatingActionButton_fmabPrompt);
            mPromptTextSize = a.getDimension(R.styleable.FloatingActionButton_fmabPromptTextSize, 80);
            mItemTextSize = a.getDimension(R.styleable.FloatingActionButton_fmabItemTextSize, 60);
            mItemTextPadding = a.getDimension(R.styleable.FloatingActionButton_fmabItemTextPadding, 20);
            mItemTextColor = a.getColor(R.styleable.FloatingActionButton_fmabItemTextColor, Color.WHITE);
            mGravity = a.getInt(R.styleable.FloatingActionButton_fmabGravity, GRAVITY_END);
            mIconPadding = a.getDimension(R.styleable.FloatingActionButton_fmabIconPadding, 25);
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

        /*if (getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL){
            if (mGravity == GRAVITY_START) mGravity = GRAVITY_END;
            else mGravity = GRAVITY_START;
        }*/
        mTextPaint.setColor(mItemTextColor);
        mTextPaint.setTextSize(mItemTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        mTextPaint.getTextBounds(mPrompt, 0, mPrompt.length(), mPromptRect);

        int width = (int) (mPromptRect.width() + mItemTextPadding*2);
        int height = (int) (mPromptRect.height() + mItemTextPadding*2);

        if (mGravity == GRAVITY_START) {
            mPromptRect.left = mButtonBounds.right;
            mPromptRect.right = mPromptRect.left + width;
        } else {
            mPromptRect.right = mButtonBounds.left;
            mPromptRect.left = mPromptRect.right - width;
        }

        mPromptRect.top = mButtonBounds.centerY() - height/2;
        mPromptRect.bottom = mButtonBounds.centerY() + height/2;
        mTextBackgroundPaint.setAlpha((int)(255*expandAnimatedValue));

        int radius = (int) (mItemTextSize * 0.25);
        itemPromptPath.reset();
        itemPromptPath.moveTo(mPromptRect.right, mPromptRect.bottom-radius);
        itemPromptPath.quadTo(mPromptRect.right, mPromptRect.bottom, mPromptRect.right-radius, mPromptRect.bottom);
        itemPromptPath.lineTo(mPromptRect.left+radius, mPromptRect.bottom);
        itemPromptPath.quadTo(mPromptRect.left, mPromptRect.bottom, mPromptRect.left, mPromptRect.bottom-radius);
        itemPromptPath.lineTo(mPromptRect.left, mPromptRect.top+radius);
        itemPromptPath.quadTo(mPromptRect.left, mPromptRect.top, mPromptRect.left+radius, mPromptRect.top);
        itemPromptPath.lineTo(mPromptRect.right-radius, mPromptRect.top);
        itemPromptPath.quadTo(mPromptRect.right, mPromptRect.top, mPromptRect.right, mPromptRect.top+radius);
        itemPromptPath.lineTo(mPromptRect.right, mPromptRect.bottom-radius);
        canvas.drawPath(itemPromptPath, mTextBackgroundPaint);

        mTextPaint.setAlpha((int) (255*expandAnimatedValue));
        canvas.drawText(mPrompt, mPromptRect.centerX(), mPromptRect.centerY() + mItemTextSize/3, mTextPaint);

        canvas.drawCircle(mButtonBounds.centerX(), mButtonBounds.centerY(), mItemButtonSize/2, mButtonPaint);

        /*getIcon().setBounds(
                (int) (item.getCenterX()-mItemButtonSize/2 + mIconPadding),
                (int) (item.getCenterY()-mItemButtonSize/2 + mIconPadding),
                (int) (item.getCenterX()+mItemButtonSize/2 - mIconPadding ),
                (int) (item.getCenterY()+mItemButtonSize/2 - mIconPadding)
        );
        item.getIcon().draw(canvas);*/

        canvas.drawCircle(mButtonBounds.centerX(), mButtonBounds.centerY(), mButtonSize/2, mButtonPaint);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final float preferedSize;
        if (mButtonSize != SIZE_NOT_SPECIFIED) preferedSize = mButtonSize;
        else preferedSize = getResources().getDisplayMetrics().widthPixels/5.25f;

        int measuredWidth = getResources().getDisplayMetrics().widthPixels;
        int measuredHeight = (int) (preferedSize+getPaddingBottom()+getPaddingTop()+ mShadowPadding *2);

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

        /*if (mGravity == GRAVITY_START) {
            mViewBounds.right = (int) (mViewBounds.left + mButtonSize);
        } else {
            mViewBounds.left = (int) (mViewBounds.right - mButtonSize);
        }*/

        mButtonBounds.left = (int) (mViewBounds.right-mButtonSize);
        mButtonBounds.top = (int) (mViewBounds.bottom - getPaddingBottom() - mButtonSize );
        mButtonBounds.right = (int) (mButtonBounds.left+mButtonSize);
        mButtonBounds.bottom = (int) (mButtonBounds.top + mButtonSize);

    }


    public void setPrompt(String prompt){
        if (mPrompt.equals(prompt)) return;
        mPrompt = prompt;
        invalidate();
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

}

package ru.rewindforce.concerts.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import ru.rewindforce.concerts.R;

public class FloatingMultiActionButton extends View {

    private static final String TAG = FloatingMultiActionButton.class.getSimpleName();

    public static final int SIZE_NOT_SPECIFIED = -1;

    private Context mContext;
    ArrayList<Item> items;
    private float mButtonSize;
    private float mSecButtonsSize;
    private Rect mViewBounds;

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

        mButtonPaint = new Paint();
        mButtonPaint.setColor(Color.BLACK);
        mButtonPaint.setStyle(Paint.Style.FILL);
        mButtonPaint.setAntiAlias(true);
        mButtonPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

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

        if (mButtonBackground == null){
            ShapeDrawable shape = new ShapeDrawable();
            shape.setShape(new Shape() {
                @Override
                public void draw(Canvas canvas, Paint paint) {
                    canvas.drawCircle(0,0,mSecButtonsSize, mButtonPaint);
                }
            });
            mButtonBackground = shape;
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
    protected void onDraw(Canvas canvas) {
        canvas.getClipBounds(mViewBounds);
        mViewBounds.left += getPaddingLeft();
        mViewBounds.top += getPaddingTop();
        mViewBounds.right -= getPaddingRight();
        mViewBounds.bottom -= getPaddingBottom();
        Log.d(TAG, "Size = "+mButtonSize);

        mButtonBackground.setBounds(0, 0, (int) mButtonSize, (int) mButtonSize);

        if (mButtonBackground instanceof ColorDrawable){
            canvas.drawCircle(
                    mViewBounds.right-mButtonSize/2,
                    mViewBounds.bottom-mButtonSize/2,
                    mButtonSize/2,
                    mButtonPaint);
        } else {
            mButtonBackground.draw(canvas);
        }
    }

    public int getItemCount(){
        return items.size();
    }

    public void setButtonBackground(Drawable drawable){
      if (mButtonBackground == drawable) return;
      mButtonBackground = drawable;
    }

    public static class Item{

        private static final String TAG = Item.class.getSimpleName();
        private final int mId;
        private String mName;
        private Drawable mIcon, mBackground;

        public Item(int id){
            mId = id;
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
    }
}

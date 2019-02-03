package ru.rewindforce.concerts.views

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import android.graphics.Path
import android.graphics.RectF
import android.widget.ImageView
import ru.rewindforce.concerts.views.MetricUtils.dpToPx
import kotlin.collections.ArrayList

class ImageGridView @JvmOverloads constructor(val ctx: Context, attrs: AttributeSet? = null, defStyle: Int = 0):
        ViewGroup(ctx, attrs, defStyle) {

    private var mediaDividerSize: Int = 6
    private var imageCount: Int = 0
    private var isAllWide = true
    private val path = Path()
    private val rect: RectF = RectF()
    private val imageViews: ArrayList<ImageView> = ArrayList()

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (imageCount > 0) if (isAllWide) layoutWideImages() else layoutImages()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size: Size = if (imageCount > 0) measureImages(widthMeasureSpec, heightMeasureSpec)
        else Size.EMPTY

        setMeasuredDimension(size.width, generateViewHeight(size.width))
    }

    private fun generateViewHeight(defaultWidth: Int): Int {
        if (imageCount > 0) {
            imageViews.forEach {
                if (it.drawable == null) return dpToPx(325f)
                else if (it.drawable.intrinsicWidth <= it.drawable.intrinsicHeight) isAllWide = false
            }
            return when (imageCount) {
                1 -> {
                    val aspect = (imageViews[0].drawable.intrinsicHeight.toFloat() / imageViews[0].drawable.intrinsicWidth.toFloat())
                    return (aspect * defaultWidth).toInt()
                }
                2 -> {
                    return if (!isAllWide) {
                        val newWidth = (defaultWidth - mediaDividerSize) / 2
                        val index = if (imageViews[0].drawable.intrinsicHeight > imageViews[1].drawable.intrinsicHeight) 0 else 1
                        val aspect = (imageViews[index].drawable.intrinsicHeight.toFloat() / imageViews[index].drawable.intrinsicWidth.toFloat())
                        (aspect * newWidth).toInt()
                    } else dpToPx(325f)
                }
                else -> {
                    dpToPx(325f)
                }
            }
        } else return 0
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        path.reset()
        rect.set(0f, 0f, w.toFloat(), h.toFloat())
        path.addRect(rect, Path.Direction.CW)
        path.close()
    }

    fun setImages(imageEntities: ArrayList<String>) {
        clearImageViews()
        initializeImageViews(imageEntities)
        requestLayout()
    }

    private fun measureImages(widthMeasureSpec: Int, heightMeasureSpec: Int): Size {
        val width: Int = MeasureSpec.getSize(widthMeasureSpec)
        val height: Int = MeasureSpec.getSize(heightMeasureSpec)

        for (index in 0 until imageCount) {
            measureImageView(index, width, height)
        }
        return Size.fromSize(width, height)
    }

    private fun measureImageView(i: Int, width: Int, height: Int) {
        imageViews[i].measure(
                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY))
        imageViews[i].scaleType = ImageView.ScaleType.CENTER_CROP
    }

    private fun layoutImages() {
        val width = measuredWidth
        val height = measuredHeight
        val halfWidth = (width) / 2
        val thirdWidth = (width) / 3
        val fourthWidth = (width) / 4
        val halfHeight = (height) / 2
        val thirdHeight = (height) / 3

        when (imageCount) {
            1 -> layoutImage(i = 0, left = 0, top = 0, right = width, bottom = height)
            2 -> {
                layoutImage(i = 0, left = 0, top = 0, right = halfWidth, bottom = height)
                layoutImage(i = 1, left = halfWidth + mediaDividerSize, top = 0, right = width, bottom = height)
            } 3 -> {
                layoutImage(i = 0, left = 0, top = 0, right = halfWidth, bottom = height)

                layoutImage(i = 1, left = halfWidth + mediaDividerSize, top = 0, right = width, bottom = halfHeight)
                layoutImage(i = 2, left = halfWidth + mediaDividerSize, top = halfHeight + mediaDividerSize, right = width, bottom = height)
            } 4 -> {
                layoutImage(i = 0, left = 0, top = 0, right = halfWidth, bottom = height)

                layoutImage(i = 1, left = halfWidth + mediaDividerSize, top = 0, right = width, bottom = thirdHeight)
                layoutImage(i = 2, left = halfWidth + mediaDividerSize, top = thirdHeight + mediaDividerSize, right = width, bottom = thirdHeight*2)
                layoutImage(i = 3, left = halfWidth + mediaDividerSize, top = thirdHeight*2 + mediaDividerSize, right = width, bottom = height)
            } 5 -> {
                layoutImage(i = 0, left = 0, top = 0, right = halfWidth, bottom = halfHeight)
                layoutImage(i = 1, left = halfWidth + mediaDividerSize, top = 0, right = width, bottom = halfHeight)

            layoutImage(i = 2, left = 0, top = halfHeight + mediaDividerSize, right = thirdWidth, bottom = height)
                layoutImage(i = 3, left = thirdWidth + mediaDividerSize, top = halfHeight + mediaDividerSize, right = thirdWidth*2, bottom = height)
                layoutImage(i = 4, left = thirdWidth*2 + mediaDividerSize, top = halfHeight + mediaDividerSize, right = width, bottom = height)
            } 6 -> {
                layoutImage(i = 0, left = 0, top = 0, right = halfWidth, bottom = halfHeight)
                layoutImage(i = 1, left = halfWidth + mediaDividerSize, top = 0, right = width, bottom = halfHeight)

                layoutImage(i = 2, left = 0, top = halfHeight + mediaDividerSize, right = fourthWidth, bottom = height)
                layoutImage(i = 3, left = fourthWidth + mediaDividerSize, top = halfHeight + mediaDividerSize, right = fourthWidth*2, bottom = height)
                layoutImage(i = 4, left = fourthWidth*2 + mediaDividerSize, top = halfHeight + mediaDividerSize, right = fourthWidth*3, bottom = height)
                layoutImage(i = 5, left = fourthWidth*3 + mediaDividerSize, top = halfHeight + mediaDividerSize, right = width, bottom = height)
            } 7 -> {
                layoutImage(i = 0, left = 0, top = 0, right = thirdWidth, bottom = halfHeight)
                layoutImage(i = 1, left = thirdWidth + mediaDividerSize, top = 0, right = thirdWidth*2, bottom = halfHeight)
                layoutImage(i = 2, left = thirdWidth*2 + mediaDividerSize, top = 0, right = thirdWidth*3, bottom = halfHeight)

                layoutImage(i = 3, left = 0, top = halfHeight + mediaDividerSize, right = fourthWidth, bottom = height)
                layoutImage(i = 4, left = fourthWidth + mediaDividerSize, top = halfHeight + mediaDividerSize, right = fourthWidth*2, bottom = height)
                layoutImage(i = 5, left = fourthWidth*2 + mediaDividerSize, top = halfHeight + mediaDividerSize, right = fourthWidth*3, bottom = height)
                layoutImage(i = 6, left = fourthWidth*3 + mediaDividerSize, top = halfHeight + mediaDividerSize, right = width, bottom = height)
            } 8 -> {
                layoutImage(i = 0, left = 0, top = 0, right = fourthWidth, bottom = halfHeight)
                layoutImage(i = 1, left = fourthWidth + mediaDividerSize, top = 0, right = fourthWidth*2, bottom = halfHeight)
                layoutImage(i = 2, left = fourthWidth*2 + mediaDividerSize, top = 0, right = fourthWidth*3, bottom = halfHeight)
                layoutImage(i = 3, left = fourthWidth*3 + mediaDividerSize, top = 0, right = fourthWidth*4, bottom = halfHeight)

                layoutImage(i = 4, left = 0, top = halfHeight + mediaDividerSize, right = fourthWidth, bottom = height)
                layoutImage(i = 5, left = fourthWidth + mediaDividerSize, top = halfHeight + mediaDividerSize, right = fourthWidth*2, bottom = height)
                layoutImage(i = 6, left = fourthWidth*2 + mediaDividerSize, top = halfHeight + mediaDividerSize, right = fourthWidth*3, bottom = height)
                layoutImage(i = 7, left = fourthWidth*3 + mediaDividerSize, top = halfHeight + mediaDividerSize, right = width, bottom = height)
            } 9 -> {
                layoutImage(i = 0, left = 0, top = 0, right = thirdWidth, bottom = thirdHeight)
                layoutImage(i = 1, left = thirdWidth + mediaDividerSize, top = 0, right = thirdWidth*2, bottom = thirdHeight)
                layoutImage(i = 2, left = thirdWidth*2 + mediaDividerSize, top = 0, right = thirdWidth*3, bottom = thirdHeight)

                layoutImage(i = 3, left = 0, top = thirdHeight + mediaDividerSize, right = halfWidth, bottom = thirdHeight*2)
                layoutImage(i = 4, left = halfWidth + mediaDividerSize, top = thirdHeight + mediaDividerSize, right = width, bottom = thirdHeight*2)

                layoutImage(i = 5, left = 0, top = thirdHeight*2 + mediaDividerSize, right = fourthWidth, bottom = height)
                layoutImage(i = 6, left = fourthWidth + mediaDividerSize, top = thirdHeight*2 + mediaDividerSize, right = fourthWidth*2, bottom = height)
                layoutImage(i = 7, left = fourthWidth*2 + mediaDividerSize, top = thirdHeight*2 + mediaDividerSize, right = fourthWidth*3, bottom = height)
                layoutImage(i = 8, left = fourthWidth*3 + mediaDividerSize, top = thirdHeight*2 + mediaDividerSize, right = width, bottom = height)
            } 10 -> {
                layoutImage(i = 0, left = 0, top = 0, right = thirdWidth, bottom = thirdHeight)
                layoutImage(i = 1, left = thirdWidth + mediaDividerSize, top = 0, right = thirdWidth*2, bottom = thirdHeight)
                layoutImage(i = 2, left = thirdWidth*2 + mediaDividerSize, top = 0, right = thirdWidth*3, bottom = thirdHeight)

                layoutImage(i = 3, left = 0, top = thirdHeight + mediaDividerSize, right = fourthWidth, bottom = thirdHeight*2)
                layoutImage(i = 4, left = fourthWidth + mediaDividerSize, top = thirdHeight + mediaDividerSize, right = fourthWidth*2, bottom = thirdHeight*2)
                layoutImage(i = 5, left = fourthWidth*2 + mediaDividerSize, top = thirdHeight + mediaDividerSize, right = fourthWidth*3, bottom = thirdHeight*2)
                layoutImage(i = 6, left = fourthWidth*3 + mediaDividerSize, top = thirdHeight + mediaDividerSize, right = width, bottom = thirdHeight*2)

                layoutImage(i = 7, left = 0, top = thirdHeight*2 + mediaDividerSize, right = thirdWidth, bottom = height)
                layoutImage(i = 8, left = thirdWidth + mediaDividerSize, top = thirdHeight*2 + mediaDividerSize, right = thirdWidth*2, bottom = height)
                layoutImage(i = 9, left = thirdWidth*2 + mediaDividerSize, top = thirdHeight*2 + mediaDividerSize, right = width, bottom = height)
            }
        }
    }

    private fun layoutWideImages() {
        val width = measuredWidth
        val height = measuredHeight
        val halfWidth = (width ) / 2
        val thirdWidth = (width) / 3
        val fourthWidth = (width) / 4
        val halfHeight = (height) / 2
        val thirdHeight = (height) / 3

        when (imageCount) {
            1 -> layoutImage(i = 0, left = 0, top = 0, right = width, bottom = height)
            2 -> {
                layoutImage(i = 0, left = 0, top = 0, right = width, bottom = halfHeight)
                layoutImage(i = 1, left = 0, top = halfHeight + mediaDividerSize, right = width, bottom = height)
        } 3 -> {
                layoutImage(i = 0, left = 0, top = 0, right = width, bottom = thirdHeight*2-thirdHeight/2)

                layoutImage(i = 1, left = 0, top = thirdHeight*2-thirdHeight/2 + mediaDividerSize, right = halfWidth, bottom = height)
                layoutImage(i = 2, left = halfWidth + mediaDividerSize, top = thirdHeight*2-thirdHeight/2 + mediaDividerSize, right = width, bottom = height)
            } 4 -> {
                layoutImage(i = 0, left = 0, top = 0, right = width, bottom = thirdHeight*2-thirdHeight/2)

                layoutImage(i = 1, left = 0, top = thirdHeight*2-thirdHeight/2 + mediaDividerSize, right = thirdWidth, bottom = height)
                layoutImage(i = 2, left = thirdWidth + mediaDividerSize, top = thirdHeight*2-thirdHeight/2 + mediaDividerSize, right = thirdWidth*2, bottom = height)
                layoutImage(i = 3, left = thirdWidth*2 + mediaDividerSize, top = thirdHeight*2-thirdHeight/2 + mediaDividerSize, right = width, bottom = height)
            } 5 -> {
                layoutImage(i = 0, left = 0, top = 0, right = width, bottom = thirdHeight*2-thirdHeight/2)

                layoutImage(i = 1, left = 0, top = thirdHeight*2-thirdHeight/2 + mediaDividerSize, right = thirdWidth, bottom = height)
                layoutImage(i = 2, left = fourthWidth + mediaDividerSize, top = thirdHeight*2-thirdHeight/2 + mediaDividerSize, right = thirdWidth*2, bottom = height)
                layoutImage(i = 3, left = fourthWidth*2 + mediaDividerSize, top = thirdHeight*2-thirdHeight/2 + mediaDividerSize, right = fourthWidth*3, bottom = height)
                layoutImage(i = 4, left = fourthWidth*3 + mediaDividerSize, top = thirdHeight*2-thirdHeight/2 + mediaDividerSize, right = width, bottom = height)
            } 6 -> {
                layoutImage(i = 0, left = 0, top = 0, right = halfWidth, bottom = thirdHeight)
                layoutImage(i = 1, left = halfWidth + mediaDividerSize, top = 0, right = width, bottom = thirdHeight)

                layoutImage(i = 2, left = 0, top = thirdHeight + mediaDividerSize, right = halfWidth, bottom = thirdHeight*2)
                layoutImage(i = 3, left = halfWidth + mediaDividerSize, top = thirdHeight + mediaDividerSize, right = width, bottom = thirdHeight*2)

                layoutImage(i = 4, left = 0, top = thirdHeight*2 + mediaDividerSize, right = halfWidth, bottom = height)
                layoutImage(i = 5, left = halfWidth + mediaDividerSize, top = thirdHeight*2 + mediaDividerSize, right = width, bottom = height)
            } 7 -> {
                layoutImage(i = 0, left = 0, top = 0, right = halfWidth, bottom = thirdHeight)
                layoutImage(i = 1, left = halfWidth + mediaDividerSize, top = 0, right = width, bottom = thirdHeight)

                layoutImage(i = 2, left = 0, top = thirdHeight + mediaDividerSize, right = thirdWidth, bottom = thirdHeight*2)
                layoutImage(i = 3, left = thirdWidth + mediaDividerSize, top = thirdHeight + mediaDividerSize, right = thirdWidth*2, bottom = thirdHeight*2)
                layoutImage(i = 4, left = thirdWidth*2 + mediaDividerSize, top = thirdHeight + mediaDividerSize, right = width, bottom = thirdHeight*2)

                layoutImage(i = 5, left = 0, top = thirdHeight*2 + mediaDividerSize, right = halfWidth, bottom = height)
                layoutImage(i = 6, left = halfWidth + mediaDividerSize, top = thirdHeight*2 + mediaDividerSize, right = width, bottom = height)
            } 8 -> {
                layoutImage(i = 0, left = 0, top = 0, right = thirdWidth, bottom = thirdHeight)
                layoutImage(i = 1, left = thirdWidth + mediaDividerSize, top = 0, right = thirdWidth*2, bottom = thirdHeight)
                layoutImage(i = 2, left = thirdWidth*2 + mediaDividerSize, top = 0, right = thirdWidth, bottom = thirdHeight)

                layoutImage(i = 3, left = 0, top = thirdHeight + mediaDividerSize, right = halfHeight, bottom = thirdHeight*2)
                layoutImage(i = 4, left = halfHeight + mediaDividerSize, top = thirdHeight + mediaDividerSize, right = width, bottom = thirdHeight*2)

                layoutImage(i = 5, left = 0, top = thirdHeight*2 + mediaDividerSize, right = thirdWidth, bottom = height)
                layoutImage(i = 6, left = thirdWidth + mediaDividerSize, top = thirdHeight*2 + mediaDividerSize, right = thirdWidth*2, bottom = height)
                layoutImage(i = 7, left = thirdWidth*2 + mediaDividerSize, top = thirdHeight*2 + mediaDividerSize, right = width, bottom = height)
            } 9 -> {
                layoutImage(i = 0, left = 0, top = 0, right = halfWidth, bottom = thirdHeight)
                layoutImage(i = 1, left = halfWidth + mediaDividerSize, top = 0, right = width, bottom = thirdHeight)

                layoutImage(i = 2, left = 0, top = thirdHeight + mediaDividerSize, right = thirdWidth, bottom = thirdHeight*2)
                layoutImage(i = 3, left = thirdWidth + mediaDividerSize, top = thirdHeight + mediaDividerSize, right = thirdWidth*2, bottom = thirdHeight*2)
                layoutImage(i = 4, left = thirdWidth*2 + mediaDividerSize, top = thirdHeight + mediaDividerSize, right = width, bottom = thirdHeight*2)

                layoutImage(i = 5, left = 0, top = thirdHeight*2 + mediaDividerSize, right = fourthWidth, bottom = height)
                layoutImage(i = 6, left = fourthWidth + mediaDividerSize, top = thirdHeight*2 + mediaDividerSize, right = fourthWidth*2, bottom = height)
                layoutImage(i = 7, left = fourthWidth*2 + mediaDividerSize, top = thirdHeight*2 + mediaDividerSize, right = fourthWidth*3, bottom = height)
                layoutImage(i = 8, left = fourthWidth*3 + mediaDividerSize, top = thirdHeight*2 + mediaDividerSize, right = width, bottom = height)
            } 10 -> {
                layoutImage(i = 0, left = 0, top = 0, right = thirdWidth, bottom = thirdHeight)
                layoutImage(i = 1, left = thirdWidth + mediaDividerSize, top = 0, right = thirdWidth*2, bottom = thirdHeight)
                layoutImage(i = 2, left = thirdWidth*2 + mediaDividerSize, top = 0, right = width, bottom = thirdHeight)

                layoutImage(i = 3, left = 0, top = thirdHeight + mediaDividerSize, right = fourthWidth, bottom = thirdHeight*2)
                layoutImage(i = 4, left = fourthWidth + mediaDividerSize, top = thirdHeight + mediaDividerSize, right = fourthWidth*2, bottom = thirdHeight*2)
                layoutImage(i = 5, left = fourthWidth*2 + mediaDividerSize, top = thirdHeight + mediaDividerSize, right = fourthWidth*3, bottom = thirdHeight*2)
                layoutImage(i = 6, left = fourthWidth*3 + mediaDividerSize, top = thirdHeight + mediaDividerSize, right = width, bottom = thirdHeight*2)

                layoutImage(i = 7, left = 0, top = thirdHeight*2 + mediaDividerSize, right = thirdWidth, bottom = height)
                layoutImage(i = 8, left = thirdWidth + mediaDividerSize, top = thirdHeight*2 + mediaDividerSize, right = thirdWidth*2, bottom = height)
                layoutImage(i = 9, left = thirdWidth*2 + mediaDividerSize, top = thirdHeight*2 + mediaDividerSize, right = width, bottom = height)
            }
        }
    }

    private fun layoutImage(i: Int, left: Int, top: Int, right: Int, bottom: Int) {
        val view = imageViews[i]
        if (view.left == left && view.top == top && view.right == right && view.bottom == bottom) {
            return
        }

        view.layout(left, top, right, bottom)
    }

    private fun clearImageViews() {
        for (index in 0 until imageCount) {
            val imageView = imageViews[index]
            imageView.visibility = View.GONE
        }
        imageCount = 0
    }

    private fun initializeImageViews(imageEntities: ArrayList<String>) {
        imageCount = Math.min(10, imageEntities.size)

        for (index in 0 until imageCount) {
            val imageView = getOrCreateImageView(index)

            val imageEntity = imageEntities[index]
            setMediaImage(imageView, imageEntity)
        }
    }

    private fun getOrCreateImageView(index: Int): ImageView {
        val imageView = ImageView(context)
        imageView.layoutParams = generateDefaultLayoutParams()
        imageViews.add(imageView)
        addView(imageView, index)

        imageView.visibility = View.VISIBLE

        return imageView
    }

    private fun setMediaImage(imageView: ImageView, imagePath: String) {
        Glide.with(ctx).load(imagePath).into(imageView)
    }

    class Size constructor(internal val width: Int = 0, internal val height: Int = 0) {
        companion object {
            internal val EMPTY = Size()

            internal fun fromSize(w: Int, h: Int): Size {
                val boundedWidth = Math.max(w, 0)
                val boundedHeight = Math.max(h, 0)
                return if (boundedWidth != 0 || boundedHeight != 0) Size(boundedWidth, boundedHeight)
                else EMPTY
            }
        }
    }
}
package ru.rewindforce.concerts.utils

import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Build
import android.util.Property
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import ru.rewindforce.concerts.HomepageActivity
import ru.rewindforce.concerts.R

fun AppCompatActivity.findFragment(tag: String): Fragment? = supportFragmentManager.findFragmentByTag(tag)

fun Fragment.findFragment(tag: String): Fragment? = childFragmentManager.findFragmentByTag(tag)

fun Fragment.openChildFragment(tag: String, fragment: Fragment, layout: Int) {
    if (findFragment(tag) == null) {
        val currentActivityFragment = activity?.supportFragmentManager?.findFragmentByTag(HomepageActivity.currentFragmentTag)
        currentActivityFragment?.childFragmentManager?.beginTransaction()?.let {
            it.replace(layout, fragment, tag)
            it.addToBackStack(tag)
            it.commit()
        }
    }
}

fun buildObjectAnimator(view: View, property: Property<View, Float>, from: Float, to: Float,
                        duration: Long = 200,
                        listener: AnimatorListenerAdapter? = null): ObjectAnimator {
    val animator = ObjectAnimator.ofFloat(view, property, from, to)
    animator.duration = duration
    animator.interpolator = LinearOutSlowInInterpolator()
    listener?.let { animator.addListener(listener) }
    return animator
}

fun AppCompatActivity.marginStatusBar(view: View) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        val statusBarHeight = resources.getDimensionPixelSize(resourceId)
        val viewParams = view.layoutParams as FrameLayout.LayoutParams
        viewParams.setMargins(0, -statusBarHeight, 0, 0)
        view.layoutParams = viewParams
    }
}

fun getDateString(ctx: Context, dateTime: DateTime) =
        dateTime.toString(DateTimeFormat.forPattern("d")) + " " +
                ctx.resources.getStringArray(R.array.month)[dateTime.monthOfYear - 1] + " " +
                dateTime.toString(DateTimeFormat.forPattern("yyyy")) + " в " +
                dateTime.toString(DateTimeFormat.forPattern("HH:mm"))
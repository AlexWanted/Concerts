package ru.rewindforce.concerts.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import jp.wasabeef.glide.transformations.BlurTransformation
import org.jetbrains.anko.support.v4.ctx
import org.joda.time.DateTime

private const val PREF_NAME: String = "concerts_prefs"
const val PREF_LOGIN: String = "pref_login"
const val PREF_UID: String = "pref_uid"
const val PREF_TOKEN: String = "pref_token"

fun Fragment.getStringPref(key: String): String? {
    val preferences: SharedPreferences = this.ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    return preferences.getString(key, "")
}

fun Activity.getStringPref(key: String): String? {
    val preferences: SharedPreferences = this.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    return preferences.getString(key, "")
}

fun Activity.hasPref(key: String): Boolean {
    val preferences: SharedPreferences = this.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    return preferences.contains(key)
}

fun loadDownsize(ctx: Context, url: String, view: ImageView, size: Int = 500) {
    Glide.with(ctx).load(url).thumbnail(0.1f).apply(RequestOptions().override(size)).into(view)
}

fun loadDownsizeBlurred(ctx: Context, url: String, view: ImageView, size: Int = 200) {
    Glide.with(ctx).load(url).thumbnail(0.1f).apply(RequestOptions()
         .apply(bitmapTransform(BlurTransformation(1, 1))).override(size)).into(view)
}

fun loadCircular(ctx: Context, url: String, view: ImageView, size: Int = 200) {
    Glide.with(ctx).load(url)
            .thumbnail(0.1f)
            .apply(RequestOptions().override(size))
            .apply(RequestOptions().circleCrop())
            .into(view)
}

fun absoluteDateTime(mills: Long): DateTime {
    val datetime = DateTime(mills)
    return DateTime(datetime.year, datetime.monthOfYear, datetime.dayOfMonth, 0, 0)
}
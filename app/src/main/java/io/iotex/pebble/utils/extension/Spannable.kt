package io.iotex.pebble.utils.extension

import android.annotation.SuppressLint
import androidx.core.graphics.TypefaceCompat
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.SpanUtils
import com.blankj.utilcode.util.Utils
import io.iotex.pebble.R
import kotlinx.android.synthetic.main.activity_device_panel.*

@SuppressLint("RestrictedApi")
fun SpanUtils.setTitleNormalStyle(text: String) = apply {
    val typeface = TypefaceCompat.createFromResourcesFontFile(Utils.getApp(), Utils.getApp().resources, R.font.space_grotesk_bold, "", 0)
    this.append(text)
        .setTypeface(typeface!!)
        .setFontSize(35, true)
        .setForegroundColor(ColorUtils.getColor(R.color.white))
}

@SuppressLint("RestrictedApi")
fun SpanUtils.setTitleHighlightStyle(text: String) = apply {
    val typeface = TypefaceCompat.createFromResourcesFontFile(Utils.getApp(), Utils.getApp().resources, R.font.space_grotesk_bold, "", 0)
    this.append(text)
        .setTypeface(typeface!!)
        .setFontSize(35, true)
        .setForegroundColor(ColorUtils.getColor(R.color.green_400))
}

package io.iotex.pebble.utils.extension

import android.annotation.SuppressLint
import androidx.core.graphics.TypefaceCompat
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.SpanUtils
import com.blankj.utilcode.util.Utils
import io.iotex.pebble.R
import kotlinx.android.synthetic.main.activity_device_panel.*

data class Style(
    val color: Int,
    val fontSize: Int
)

@SuppressLint("RestrictedApi")
fun SpanUtils.setTitleNormalStyle(text: String, style: Style) = apply {
    val typeface = TypefaceCompat.createFromResourcesFontFile(Utils.getApp(), Utils.getApp().resources, R.font.space_grotesk_bold, "", 0)
    this.append(text)
        .setTypeface(typeface!!)
        .setFontSize(style.fontSize, true)
        .setForegroundColor(ColorUtils.getColor(style.color))
}

@SuppressLint("RestrictedApi")
fun SpanUtils.setTitleHighlightStyle(text: String, style: Style) = apply {
    val typeface = TypefaceCompat.createFromResourcesFontFile(Utils.getApp(), Utils.getApp().resources, R.font.space_grotesk_bold, "", 0)
    this.append(text)
        .setTypeface(typeface!!)
        .setFontSize(style.fontSize, true)
        .setForegroundColor(ColorUtils.getColor(style.color))
}

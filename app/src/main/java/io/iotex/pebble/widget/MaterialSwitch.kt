package io.iotex.pebble.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.widget.Switch
import androidx.core.graphics.TypefaceCompat
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.Utils
import io.iotex.pebble.R

@SuppressLint("RestrictedApi")
class MaterialSwitch(context: Context, attrs: AttributeSet): Switch(context, attrs) {

    private val mTextPaint by lazy { TextPaint(Paint.ANTI_ALIAS_FLAG) }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.MaterialSwitch)
        val secondaryTextColor = a.getColor(R.styleable.MaterialSwitch_secondaryTextColor, ColorUtils.getColor(R.color.white_alpha_50))
        val secondaryTextSize = a.getDimension(R.styleable.MaterialSwitch_secondaryTextSize, 18F)
        val secondaryFont = a.getInteger(R.styleable.MaterialSwitch_secondaryTextFont, R.font.space_grotesk_bold)
        a.recycle()

        val typeface = TypefaceCompat.createFromResourcesFontFile(Utils.getApp(), Utils.getApp().resources, secondaryFont, "", 0)

        mTextPaint.color = secondaryTextColor
        mTextPaint.textSize = secondaryTextSize
        mTextPaint.textAlign = Paint.Align.CENTER
        mTextPaint.typeface = typeface
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val fontMetrics = mTextPaint.fontMetrics
        val top = fontMetrics.top
        val bottom = fontMetrics.bottom

        val baseLineY = height / 2 - top / 2 - bottom / 2

        if (isChecked) {
            canvas?.drawText(textOff.toString(), (width / 4).toFloat(), baseLineY, mTextPaint)
        } else {
            canvas?.drawText(textOn.toString(), (width / 4 * 3).toFloat(), baseLineY, mTextPaint)
        }

    }




}
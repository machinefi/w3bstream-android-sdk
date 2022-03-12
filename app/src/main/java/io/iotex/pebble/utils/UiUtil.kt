package io.iotex.pebble.utils

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bigkoo.pickerview.view.OptionsPickerView
import com.blankj.utilcode.util.ScreenUtils
import io.iotex.pebble.R

fun <T> getPickerBuilder(context: Context, title: String, selectOpt: Int = 0, listener: OnOptionsSelectListener): OptionsPickerView<T> {
    val builder = OptionsPickerBuilder(context, listener)
        .setDividerColor(Color.WHITE)
        .setSubmitColor(context.getColor(R.color.gray_300))
        .setCancelColor(context.getColor(R.color.gray_300))
        .setTextColorCenter(context.getColor(R.color.gray_300))
        .setTitleColor(context.getColor(R.color.gray_300))
        .setBgColor(context.getColor(R.color.teal_800))
        .setDividerColor(context.getColor(R.color.white_alpha_6))
        .setTitleBgColor(context.getColor(R.color.teal_800))
        .setContentTextSize(14)
        .setTitleText(title)
        .setSelectOptions(selectOpt)
        .setSubmitText(context.getString(R.string.confirm))
        .setCancelText(context.getString(R.string.cancel))
        .setTitleSize(16)
        .isDialog(true)
        .build<T>()

    val dialog = builder.dialog
    if (dialog != null) {
        val params = FrameLayout.LayoutParams(
            ScreenUtils.getScreenWidth(),
            ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM)

        params.leftMargin = 0
        params.rightMargin = 0
        builder.dialogContainerLayout.layoutParams = params

        val window = dialog.window
        window?.setWindowAnimations(R.style.picker_view_slide_anim)
        window?.setGravity(Gravity.BOTTOM)

        window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        window?.statusBarColor = Color.TRANSPARENT

    }
    return builder
}

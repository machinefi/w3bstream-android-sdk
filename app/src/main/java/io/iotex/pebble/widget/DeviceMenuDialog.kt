package io.iotex.pebble.widget

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.blankj.utilcode.util.Utils
import io.iotex.pebble.R
import io.iotex.pebble.utils.extension.dp2px

class DeviceMenuDialog {

    private val mPopupWindow: PopupWindow
    private val mLlHistory: View
    private val mLlOwnership: View
    private val mLlAbout: View
    private val mLlSetting: View

    init {
        val layout =
            LayoutInflater.from(Utils.getApp()).inflate(R.layout.dialog_divice_menu, null)

        mPopupWindow = PopupWindow(
            layout,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            this.setBackgroundDrawable(ColorDrawable(0))
            this.isFocusable = true
            this.isOutsideTouchable = true
        }

        mLlHistory = layout.findViewById(R.id.mLlHistory)
        mLlOwnership = layout.findViewById(R.id.mLlOwnership)
        mLlAbout = layout.findViewById(R.id.mLlAbout)
        mLlSetting = layout.findViewById(R.id.mLlSetting)
    }

    fun setHistoryListener(l: () -> Unit) = apply {
        mLlHistory.setOnClickListener {
            l.invoke()
            dismiss()
        }
    }

    fun setOwnershipListener(l: () -> Unit) = apply {
        mLlOwnership.setOnClickListener {
            l.invoke()
            dismiss()
        }
    }

    fun setAboutListener(l: () -> Unit) = apply {
        mLlAbout.setOnClickListener {
            l.invoke()
            dismiss()
        }
    }

    fun setSettingListener(l: () -> Unit) = apply {
        mLlSetting.setOnClickListener {
            l.invoke()
            dismiss()
        }
    }

    fun show(parent: View) {
        if (!mPopupWindow.isShowing) {
            mPopupWindow.showAsDropDown(parent, 16.dp2px(), 0)
        }
    }

    fun dismiss() {
        if (mPopupWindow.isShowing) {
            mPopupWindow.dismiss()
        }
    }
}
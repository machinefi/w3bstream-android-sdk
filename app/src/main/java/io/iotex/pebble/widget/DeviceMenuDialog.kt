package io.iotex.pebble.widget

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.Utils
import io.iotex.pebble.R
import io.iotex.pebble.pages.AboutActivity
import io.iotex.pebble.pages.HistoryActivity
import io.iotex.pebble.pages.SettingActivity
import org.jetbrains.anko.startActivity

class DeviceMenuDialog {

    private val mPopupWindow: PopupWindow
    private lateinit var mLlHistory: View
    private lateinit var mLlAbout: View
    private lateinit var mLlSetting: View

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
        mLlAbout = layout.findViewById(R.id.mLlAbout)
        mLlSetting = layout.findViewById(R.id.mLlSetting)
    }

    fun setHistoryListener(l: () -> Unit): DeviceMenuDialog {
        mLlHistory.setOnClickListener {
            l.invoke()
            mPopupWindow.dismiss()
        }
        return this
    }

    fun setAboutListener(l: () -> Unit): DeviceMenuDialog {
        mLlAbout.setOnClickListener {
            l.invoke()
            mPopupWindow.dismiss()
        }
        return this
    }

    fun setSettingListener(l: () -> Unit): DeviceMenuDialog {
        mLlSetting.setOnClickListener {
            l.invoke()
            mPopupWindow.dismiss()
        }
        return this
    }

    fun show(parent: View) {
        mPopupWindow.showAsDropDown(parent)
    }
}
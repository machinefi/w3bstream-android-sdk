package io.iotex.pebble.widget

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.blankj.utilcode.util.ScreenUtils
import io.iotex.pebble.R
import io.iotex.pebble.utils.extension.dp2px

abstract class BaseDialog(val context: Context, resId: Int) {

    protected val mDialog: Dialog
    private val mContentView: View

    init {
        mContentView = LayoutInflater.from(context).inflate(resId, null)
        mDialog = Dialog(context, R.style.CommonDialog)
        mDialog.setContentView(mContentView)

        val params = mDialog.window?.attributes
        params?.width = ScreenUtils.getScreenWidth() - (15 * 2).dp2px()
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
        params?.gravity = Gravity.BOTTOM
        params?.windowAnimations = R.style.DialogAnimation
        mDialog.window?.attributes = params
    }

    protected fun <T: View> findView(id: Int): T {
        return mContentView.findViewById(id)
    }

    fun cancellableOnTouchOutside(cancellable: Boolean) = apply {
        mDialog.setCanceledOnTouchOutside(cancellable)
    }

    fun show() {
        if (context is Activity && !context.isFinishing && !mDialog.isShowing) {
            mDialog.show()
        }
    }

    fun dismiss() {
        if (mDialog.isShowing){
            mDialog.dismiss()
        }
    }

}
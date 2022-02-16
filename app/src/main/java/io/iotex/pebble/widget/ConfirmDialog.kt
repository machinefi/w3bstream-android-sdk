package io.iotex.pebble.widget

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.TextView
import com.blankj.utilcode.util.ScreenUtils
import io.iotex.pebble.R
import io.iotex.pebble.utils.extension.dp2px

class ConfirmDialog(val context: Context) {

    private val dialog: Dialog
    private val mTvContent: TextView
    private val mTvConfirm: TextView

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_confirm, null)
        this.mTvContent = view.findViewById(R.id.mTvContent)
        this.mTvConfirm = view.findViewById(R.id.mTvConfirm)

        dialog = Dialog(context, R.style.CommonDialog)
        dialog.setContentView(view)

        val params = dialog.window?.attributes
        params?.width = ScreenUtils.getScreenWidth() - (32 * 2).dp2px()
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window?.attributes = params
    }

    fun setContent(content: String): ConfirmDialog {
        mTvContent.text = content
        return this
    }

    fun cancellableOnTouchOutside(cancellable: Boolean): ConfirmDialog {
        dialog.setCanceledOnTouchOutside(cancellable)
        return this
    }

    fun setPositiveButton(positive: String, callback: (() -> Unit)? = null): ConfirmDialog {
        mTvConfirm.text = positive
        mTvConfirm.setOnClickListener {
            callback?.invoke()
            dialog.dismiss()
        }
        return this
    }

    fun show() {
        if (context is Activity && !context.isFinishing && !dialog.isShowing) {
            dialog.show()
        }
    }

    fun dismiss() {
        if (dialog.isShowing){
            dialog.dismiss()
        }
    }

}

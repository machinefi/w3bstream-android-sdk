package com.machinefi.metapebble.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.machinefi.metapebble.R
import com.machinefi.metapebble.utils.extension.gone
import com.machinefi.metapebble.utils.extension.visible

class UpdateDialog(context: Context): BaseDialog(context, R.layout.dialog_update) {

    private val mTvContent: TextView = findView(R.id.mTvContent)
    private val mTvConfirm: TextView = findView(R.id.mTvConfirm)
    private val mIvClose: ImageView = findView(R.id.mIvClose)

    init {
        mIvClose.setOnClickListener {
            dismiss()
        }
    }

    fun setContent(content: String) = apply {
        mTvContent.text = content
    }

    fun setUrl(url: String) = apply {
        mTvConfirm.setOnClickListener {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
    }

    override fun cancellableOnTouchOutside(cancellable: Boolean): UpdateDialog = apply {
        super.cancellableOnTouchOutside(cancellable)
        if (cancellable) {
            mIvClose.visible()
        } else {
            mIvClose.gone()
        }
    }
}

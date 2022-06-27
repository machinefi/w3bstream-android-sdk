package com.machinefi.metapebble.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.TextView
import com.machinefi.metapebble.R

class UpdateDialog(context: Context): BaseDialog(context, R.layout.dialog_update) {

    private val mTvContent: TextView = findView(R.id.mTvContent)
    private val mTvConfirm: TextView = findView(R.id.mTvConfirm)

    init {
        findView<View>(R.id.mIvClose).setOnClickListener {
            dismiss()
        }


    }

    fun setContent(content: String) = apply {
        mTvContent.text = content
    }

    fun setUrl(url: String) = apply {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}

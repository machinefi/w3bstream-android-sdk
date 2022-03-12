package io.iotex.pebble.widget

import android.content.Context
import android.view.View
import android.widget.TextView
import io.iotex.pebble.R

class PromptDialog(context: Context): BaseDialog(context, R.layout.dialog_prompt) {

    private val mTvTitle: TextView = findView(R.id.mTvContent)
    private val mTvContent: TextView = findView(R.id.mTvContent)
    private val mTvConfirm: TextView = findView(R.id.mTvConfirm)

    init {
        findView<View>(R.id.mIvClose).setOnClickListener {
            dismiss()
        }
    }

    fun setTitle(title: String) = apply {
        mTvTitle.text = title
    }

    fun setContent(content: String) = apply {
        mTvContent.text = content
    }

    fun setPositiveButton(positive: String, callback: (() -> Unit)? = null) = apply {
        mTvConfirm.text = positive
        mTvConfirm.setOnClickListener {
            callback?.invoke()
            dismiss()
        }
    }

}

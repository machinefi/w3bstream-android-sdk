package com.machinefi.metapebble.widget

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.machinefi.metapebble.R

class LoadingDialog(context: Context): BaseDialog(context, R.layout.dialog_loading) {

    private val mProgress: ProgressBar = findView(R.id.mProgress)

    init {
        val params = mDialog.window?.attributes
        params?.gravity = Gravity.CENTER
        params?.windowAnimations = 0
        mDialog.window?.attributes = params
    }

}

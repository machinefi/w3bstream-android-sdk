package com.machinefi.sample

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialog

class LoadingDialog @JvmOverloads constructor(
    context: Context,
    themeId: Int = R.style.LoadingDialogStyle
) : AppCompatDialog(context, themeId) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_loading)
    }

    class Builder(private val context: Context) {
        private var isCancelable = true
        private var isCancelOutside = true


        fun setCancelable(isCancelable: Boolean): Builder {
            this.isCancelable = isCancelable
            return this
        }

        fun setCancelOutside(isCancelOutside: Boolean): Builder {
            this.isCancelOutside = isCancelOutside
            return this
        }

        fun create(): LoadingDialog {
            val loadingDialog = LoadingDialog(context)
            loadingDialog.setCancelable(isCancelable)
            loadingDialog.setCanceledOnTouchOutside(isCancelOutside)
            return loadingDialog
        }

    }
}
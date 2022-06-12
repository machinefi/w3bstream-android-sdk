package io.iotex.pebble.widget

import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.blankj.utilcode.util.RegexUtils
import com.blankj.utilcode.util.SPUtils
import io.iotex.pebble.R
import io.iotex.pebble.constant.SP_KEY_SERVER_URL

class ServerDialog(context: Context): BaseDialog(context, R.layout.dialog_server) {

    private val mEtServer: EditText = findView(R.id.mEtServer)
    private val mTvConfirm: TextView = findView(R.id.mTvConfirm)

    init {
        findView<View>(R.id.mIvClose).setOnClickListener {
            dismiss()
        }

        val serverUrl = SPUtils.getInstance().getString(SP_KEY_SERVER_URL)
        mEtServer.setText(serverUrl)

        mTvConfirm.setOnClickListener {
            val url = mEtServer.text.toString().trim()
            if (RegexUtils.isURL(url)) {
                SPUtils.getInstance().put(SP_KEY_SERVER_URL, url)
                dismiss()
            }
        }
    }
}

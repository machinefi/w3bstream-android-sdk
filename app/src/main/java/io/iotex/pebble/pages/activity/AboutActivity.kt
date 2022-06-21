package io.iotex.pebble.pages.activity

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ClipboardUtils
import io.iotex.core.base.BaseActivity
import io.iotex.pebble.R
import io.iotex.pebble.constant.PebbleStore
import io.iotex.pebble.module.db.entries.DeviceEntry
import io.iotex.pebble.module.mqtt.EncryptUtil
import io.iotex.pebble.utils.extension.ellipsis
import io.iotex.pebble.utils.extension.toast
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity: BaseActivity(R.layout.activity_about) {

    private val mDevice by lazy {
        PebbleStore.mDevice
    }

    override fun initView(savedInstanceState: Bundle?) {
        mTvImei.text = " ${mDevice?.imei}"
        mTvSn.text = "${mDevice?.sn}"
        mTvVersion.text = "Developer Preview V0.1"
//        mTvAddress.text = "${EncryptUtil.formatAddress(mDevice?.address ?: "").ellipsis(6, 8)}"

//        mIvImeiCopy.setOnClickListener {
//            ClipboardUtils.copyText(mDevice?.imei)
//            getString(R.string.success).toast()
//        }
//        mIvSnCopy.setOnClickListener {
//            ClipboardUtils.copyText(mDevice?.sn)
//            getString(R.string.success).toast()
//        }
        mIvAddressCopy.setOnClickListener {
//            ClipboardUtils.copyText(EncryptUtil.formatAddress(mDevice?.address ?: ""))
            getString(R.string.success).toast()
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
    }

    override fun registerObserver() {
    }
}
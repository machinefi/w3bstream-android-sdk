package io.iotex.pebble.pages.activity

import android.os.Bundle
import com.blankj.utilcode.util.ClipboardUtils
import io.iotex.core.base.BaseActivity
import io.iotex.pebble.R
import io.iotex.pebble.module.db.entries.DeviceEntry
import io.iotex.pebble.module.mqtt.EncryptUtil
import io.iotex.pebble.utils.extension.ellipsis
import io.iotex.pebble.utils.extension.toast
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity: BaseActivity(R.layout.activity_about) {

    private val mDevice by lazy {
        intent.getSerializableExtra(KEY_DEVICE) as? DeviceEntry
    }

    override fun initView(savedInstanceState: Bundle?) {
        mTvImei.text = " ${mDevice?.imei}"
        mTvSn.text = "${mDevice?.sn}"
        mTvAddress.text = "${EncryptUtil.formatAddress(mDevice?.address ?: "").ellipsis(6, 8)}"

//        mIvImeiCopy.setOnClickListener {
//            ClipboardUtils.copyText(mDevice?.imei)
//            getString(R.string.success).toast()
//        }
//        mIvSnCopy.setOnClickListener {
//            ClipboardUtils.copyText(mDevice?.sn)
//            getString(R.string.success).toast()
//        }
        mIvAddressCopy.setOnClickListener {
            ClipboardUtils.copyText(EncryptUtil.formatAddress(mDevice?.address ?: ""))
            getString(R.string.success).toast()
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
    }

    override fun registerObserver() {
    }

    companion object {
        const val KEY_DEVICE = "key_device"
    }
}
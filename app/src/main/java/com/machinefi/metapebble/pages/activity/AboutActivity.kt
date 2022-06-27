package com.machinefi.metapebble.pages.activity

import android.os.Bundle
import com.machinefi.core.base.BaseActivity
import com.machinefi.metapebble.R
import com.machinefi.metapebble.constant.PebbleStore
import com.machinefi.metapebble.utils.extension.toast
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity: BaseActivity(R.layout.activity_about) {

    private val mDevice by lazy {
        PebbleStore.mDevice
    }

    override fun initView(savedInstanceState: Bundle?) {
        mTvImei.text = " ${mDevice?.imei}"
        mTvSn.text = "${mDevice?.sn}"
        mTvVersion.text = "Developer Preview V1.0.0"
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
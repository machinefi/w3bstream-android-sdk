package io.iotex.pebble.pages.activity

import android.os.Bundle
import io.iotex.core.base.BaseActivity
import io.iotex.pebble.R
import io.iotex.pebble.constant.PebbleStore
import io.iotex.pebble.module.db.entries.DeviceEntry
import io.iotex.pebble.module.walletconnect.WcKit
import kotlinx.android.synthetic.main.activity_ownership.*
import org.jetbrains.anko.startActivity

class OwnershipActivity: BaseActivity(R.layout.activity_ownership) {

    private val mDevice by lazy {
        PebbleStore.mDevice
    }

    override fun initView(savedInstanceState: Bundle?) {
        mTvTransfer.setOnClickListener {
            startActivity<TransferOwnershipActivity>()
        }
        mTvCancel.setOnClickListener {
            onBackPressed()
        }
        mDevice?.let {
            mTvImei.text = it.imei
            mTvSN.text = it.sn
            mTvAddress.text = it.address
            mTvWalletAddress.text = it.owner
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
    }

    override fun registerObserver() {
    }
}
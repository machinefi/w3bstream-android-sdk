package com.machinefi.metapebble.pages.activity

import android.os.Bundle
import com.machinefi.core.base.BaseActivity
import com.machinefi.metapebble.R
import com.machinefi.metapebble.constant.PebbleStore
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
            mTvWalletAddress.text = it.owner
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
    }

    override fun registerObserver() {
    }
}
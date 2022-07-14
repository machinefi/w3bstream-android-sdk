package com.machinefi.metapebble.pages.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.machinefi.core.base.BaseActivity
import com.machinefi.metapebble.R
import com.machinefi.metapebble.constant.APK_DOWNLOAD_URL
import com.machinefi.metapebble.module.walletconnect.WalletConnector
import kotlinx.android.synthetic.main.activity_transfer_ownership.*

class TransferOwnershipActivity: BaseActivity(R.layout.activity_transfer_ownership) {

    override fun initView(savedInstanceState: Bundle?) {
        mTvFrom.text = WalletConnector.walletAddress
        mTvDownloadIopay.setOnClickListener {
            val uri: Uri = Uri.parse(APK_DOWNLOAD_URL)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
    }

    override fun registerObserver() {
    }
}
package com.machinefi.metapebble.pages.activity

import android.content.Intent
import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.machinefi.core.base.BaseActivity
import com.machinefi.metapebble.R

class DeeplinkActivity: BaseActivity(R.layout.activity_deeplink) {

    override fun beforeInflate(savedInstanceState: Bundle?) {
        super.beforeInflate(savedInstanceState)
        val exist = ActivityUtils.isActivityExistsInStack(DevicePanelActivity::class.java)
        if (exist) {
            startActivity(Intent(this, DevicePanelActivity::class.java).apply {
                data = intent.data
            })
        } else {
            startActivity(Intent(this, SplashActivity::class.java).apply {
                data = intent.data
            })
        }
        finish()
    }

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initData(savedInstanceState: Bundle?) {
    }

    override fun registerObserver() {
    }
}
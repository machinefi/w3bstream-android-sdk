package com.machinefi.metapebble.pages.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.machinefi.core.base.BaseActivity
import com.machinefi.metapebble.R
import com.machinefi.metapebble.constant.PebbleStore
import com.machinefi.metapebble.module.viewmodel.AppVM
import com.machinefi.metapebble.module.viewmodel.PebbleVM
import org.jetbrains.anko.startActivity

class SplashActivity : BaseActivity(R.layout.activity_splash) {

    private val mPebbleVM by lazy {
        ViewModelProvider(this, mVmFactory)[PebbleVM::class.java]
    }
    private val mAppVM by lazy {
        ViewModelProvider(this, mVmFactory)[AppVM ::class.java]
    }

    override fun beforeInflate(savedInstanceState: Bundle?) {
        super.beforeInflate(savedInstanceState)
        window.setBackgroundDrawable(null)
    }

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initData(savedInstanceState: Bundle?) {
        mPebbleVM.queryDeviceList()
    }

    override fun registerObserver() {
        mPebbleVM.mDeviceListLD.observe(this) {
            if (!it.isNullOrEmpty()) {
                PebbleStore.setDevice(it[0])
                startActivity<DevicePanelActivity>()
            } else {
                startActivity<CreateActivity>()
            }
            finish()
        }
    }
}
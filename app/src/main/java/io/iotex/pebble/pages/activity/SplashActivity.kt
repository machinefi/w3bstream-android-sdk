package io.iotex.pebble.pages.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import io.iotex.core.base.BaseActivity
import io.iotex.pebble.R
import io.iotex.pebble.constant.PebbleStore
import io.iotex.pebble.module.viewmodel.ActivateVM
import io.iotex.pebble.module.viewmodel.PebbleVM
import org.jetbrains.anko.startActivity

class SplashActivity : BaseActivity(R.layout.activity_splash) {

    private val mPebbleVM by lazy {
        ViewModelProvider(this, mVmFactory)[PebbleVM::class.java]
    }

    private val mActivateVM by lazy {
        ViewModelProvider(this, mVmFactory)[ActivateVM::class.java]
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
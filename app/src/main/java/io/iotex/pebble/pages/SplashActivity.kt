package io.iotex.pebble.pages

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import io.iotex.core.base.BaseActivity
import io.iotex.pebble.R
import io.iotex.pebble.module.viewmodel.WalletVM
import org.jetbrains.anko.startActivity

class SplashActivity : BaseActivity(R.layout.activity_splash) {

    private val mWalletVM by lazy {
        ViewModelProvider(this)[WalletVM::class.java]
    }

    override fun beforeInflate(savedInstanceState: Bundle?) {
        super.beforeInflate(savedInstanceState)
        window.setBackgroundDrawable(null)
    }

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initData(savedInstanceState: Bundle?) {
        mWalletVM.queryDeviceList()
    }

    override fun registerObserver() {
        mWalletVM.mDeviceListLiveData.observe(this) {
            if (!it.isNullOrEmpty()) {
//                startActivity<WalletConnectActivity>(
//                    DevicePanelActivity.KEY_DEVICE to it[0]
//                )
            } else {
//                startActivity<CreateActivity>()
            }
//            finish()
        }
    }
}
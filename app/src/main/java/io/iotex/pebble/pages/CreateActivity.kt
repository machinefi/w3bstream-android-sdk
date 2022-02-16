package io.iotex.pebble.pages

import android.os.Bundle
import io.iotex.core.base.BaseActivity
import io.iotex.pebble.R
import io.iotex.pebble.pages.DevicePanelActivity.Companion.KEY_DEVICE
import io.iotex.pebble.utils.DeviceHelper
import kotlinx.android.synthetic.main.activity_create.*
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread
import java.util.*

class CreateActivity: BaseActivity() {

    override fun layoutResourceID(savedInstanceState: Bundle?): Int {
        return R.layout.activity_create
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBtnCreate.setOnClickListener {
            doAsync {
                val device = DeviceHelper.createDevice()
                uiThread {
                    this@CreateActivity.startActivity<DevicePanelActivity>(
                        KEY_DEVICE to device
                    )
                    finish()
                }
            }
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
    }

    override fun registerObserver() {
    }

}
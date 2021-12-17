package io.iotex.pebble.pages

import android.os.Bundle
import io.iotex.core.base.BaseActivity
import io.iotex.pebble.R

class SplashActivity: BaseActivity() {

    override fun layoutResourceID(savedInstanceState: Bundle?): Int {
        return R.layout.activity_splash
    }

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initData(savedInstanceState: Bundle?) {
    }

    override fun registerObserver() {
    }

}
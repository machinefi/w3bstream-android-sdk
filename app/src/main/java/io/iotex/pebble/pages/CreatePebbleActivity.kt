package io.iotex.pebble.pages

import android.os.Bundle
import io.iotex.core.base.BaseActivity
import io.iotex.pebble.R

class CreatePebbleActivity: BaseActivity() {
    override fun layoutResourceID(savedInstanceState: Bundle?): Int {
        return R.layout.activity_create_pebble
    }

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initData(savedInstanceState: Bundle?) {
    }

    override fun registerObserver() {
    }
}
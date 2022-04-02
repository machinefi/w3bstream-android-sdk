package io.iotex.pebble.pages.activity

import android.content.Intent
import android.os.Bundle
import io.iotex.core.base.BaseActivity
import io.iotex.pebble.R
import kotlinx.android.synthetic.main.activity_activate_complete.*
import org.jetbrains.anko.startActivity

class ActivateCompleteActivity: BaseActivity(R.layout.activity_activate_complete) {

    override fun initView(savedInstanceState: Bundle?) {
        mTvBackHome.setOnClickListener {
            startActivity(Intent(this, DevicePanelActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            })
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
    }

    override fun registerObserver() {
    }
}
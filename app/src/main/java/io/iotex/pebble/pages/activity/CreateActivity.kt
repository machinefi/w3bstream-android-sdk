package io.iotex.pebble.pages.activity

import android.os.Bundle
import com.blankj.utilcode.util.FragmentUtils
import io.iotex.core.base.BaseActivity
import io.iotex.pebble.R
import io.iotex.pebble.module.db.entries.DeviceEntry
import io.iotex.pebble.pages.activity.DevicePanelActivity.Companion.KEY_DEVICE
import io.iotex.pebble.pages.fragment.LoadingFragment
import io.iotex.pebble.utils.DeviceHelper
import io.iotex.pebble.utils.extension.loadGif
import kotlinx.android.synthetic.main.activity_create.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread

class CreateActivity : BaseActivity(R.layout.activity_create) {

    override fun initView(savedInstanceState: Bundle?) {
        val metaPebble = getString(R.string.meta_pebble)
        val tips = listOf(metaPebble, metaPebble, metaPebble, metaPebble, metaPebble, metaPebble)
        mTvMarquee.text = tips.joinToString(" ")
        mTvMarquee.isSelected = true

        mIvDiamond.loadGif(R.drawable.diamond_dynamic, 0)
        mBtnCreate.setOnClickListener {
            var device: DeviceEntry? = null
            LoadingFragment()
                .renderTitle(getString(R.string.prepare_tips), getString(R.string.meta_pebble))
                .setStartCallback {
                    doAsync {
                        device = DeviceHelper.createDevice()
                    }
                }
                .setCompleteCallback {
                    this@CreateActivity.startActivity<DevicePanelActivity>(
                        KEY_DEVICE to device
                    )
                    finish()
                }
                .show(supportFragmentManager, R.id.mRlContainer)
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
    }

    override fun registerObserver() {
    }

}
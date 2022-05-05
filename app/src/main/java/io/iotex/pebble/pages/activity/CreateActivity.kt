package io.iotex.pebble.pages.activity

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import io.iotex.core.base.BaseActivity
import io.iotex.pebble.R
import io.iotex.pebble.constant.PebbleStore
import io.iotex.pebble.module.db.entries.DeviceEntry
import io.iotex.pebble.pages.fragment.LoadingFragment
import io.iotex.pebble.utils.DeviceHelper
import io.iotex.pebble.utils.extension.loadGif
import kotlinx.android.synthetic.main.activity_create.*
import kotlinx.coroutines.launch
import org.jetbrains.anko.startActivity

class CreateActivity : BaseActivity(R.layout.activity_create) {

    private val mLoading by lazy {
        LoadingFragment()
            .renderTitle(getString(R.string.prepare_tips), getString(R.string.meta_pebble))
            .setStartCallback {
                createDevice()
            }
            .setCompleteCallback {
                createCompletely()
            }
    }

    private var mDevice: DeviceEntry? = null

    override fun initView(savedInstanceState: Bundle?) {
        val metaPebble = getString(R.string.meta_pebble)
        val tips = listOf(metaPebble, metaPebble, metaPebble, metaPebble, metaPebble, metaPebble)
        mTvMarquee.text = tips.joinToString(" ")
        mTvMarquee.isSelected = true
        mIvDiamond.loadGif(R.drawable.diamond_dynamic, 0)
        mBtnCreate.setOnClickListener {
            mLoading.start(supportFragmentManager, R.id.mRlContainer)
        }
    }

    private fun createDevice() {
        lifecycleScope.launch {
            runCatching {
                val device = DeviceHelper.createDevice()
                mDevice = device
            }.onSuccess {
                mLoading.complete()
            }.onFailure {
                mLoading.dismiss()
            }

        }
    }

    private fun createCompletely() {
        mDevice?.also { PebbleStore.setDevice(it) }
        this@CreateActivity.startActivity<DevicePanelActivity>()
        finish()
    }

    override fun initData(savedInstanceState: Bundle?) {
    }

    override fun registerObserver() {
    }

}
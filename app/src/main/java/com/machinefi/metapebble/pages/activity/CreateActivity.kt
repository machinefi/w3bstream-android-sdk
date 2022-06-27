package com.machinefi.metapebble.pages.activity

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.machinefi.core.base.BaseActivity
import com.machinefi.metapebble.R
import com.machinefi.metapebble.constant.PebbleStore
import com.machinefi.metapebble.module.db.entries.DeviceEntry
import com.machinefi.metapebble.pages.fragment.LoadingFragment
import com.machinefi.metapebble.utils.DeviceHelper
import com.machinefi.metapebble.utils.extension.loadGif
import com.machinefi.metapebble.utils.extension.toast
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
                it.message?.toast()
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
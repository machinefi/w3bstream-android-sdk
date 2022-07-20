package com.machinefi.metapebble.pages.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.Utils
import com.machinefi.core.base.BaseActivity
import com.machinefi.metapebble.R
import com.machinefi.metapebble.constant.PebbleStore
import com.machinefi.metapebble.module.viewmodel.ActivateVM
import com.machinefi.metapebble.module.viewmodel.PebbleVM
import com.machinefi.metapebble.module.walletconnect.WalletConnector
import com.machinefi.metapebble.utils.extension.*
import com.machinefi.metapebble.widget.DeviceMenuDialog
import com.machinefi.metapebble.widget.LoadingDialog
import com.machinefi.metapebble.widget.PromptDialog
import kotlinx.android.synthetic.main.activity_device_panel.*
import kotlinx.android.synthetic.main.include_bar.*
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.startActivity

class DevicePanelActivity : BaseActivity(R.layout.activity_device_panel) {

    private val mActivateVM by lazy {
        ViewModelProvider(this, mVmFactory)[ActivateVM::class.java]
    }

    private val mPebbleVM by lazy {
        ViewModelProvider(this, mVmFactory)[PebbleVM::class.java]
    }

    private val mProgress by lazy {
        LoadingDialog(this)
    }

    private val mDevice by lazy {
        PebbleStore.mDevice
    }

    private var mNeedResponse = false

    private var mIsActivated = false

    override fun initView(savedInstanceState: Bundle?) {
        mTvImei.text = "IMEI: ${mDevice?.imei}"
        mTvSn.text = "SN: ${mDevice?.sn}"

        renderMenu()
        renderTips()

        mTvAuthorize.setOnClickListener {
            PromptDialog(this)
                .setTitle(getString(R.string.authorize_to_pop))
                .setContent(getString(R.string.authorize_to_pop_tips_01))
                .setPositiveButton(getString(R.string.authorize))
                .setCaption(getString(R.string.try_later), true)
                .show()
        }

        mSwitch.setOnCheckedChangeListener { _, isChecked ->
            mDevice?.let {
                if (isChecked) {
                    mPebbleVM.powerOn(it)
                } else {
                    mPebbleVM.powerOff(it)
                }
            }
        }
    }

    private fun renderMenu() {
        mIvMenu.visible()
        mIvMenu.setOnClickListener {
            DeviceMenuDialog()
                .setHistoryListener {
                    startActivity<HistoryActivity>()
                }
                .setOwnershipListener {
                    if (mIsActivated) {
                        startActivity<OwnershipActivity>()
                    } else {
                        getString(R.string.please_activate_pebble).toast()
                    }
                }
                .setAboutListener {
                    startActivity<AboutActivity>()
                }
                .setSettingListener {
                    startActivity<SettingActivity>()
                }
                .show(mIvMenu)
        }
    }

    private fun renderTips() {
        val tipsStr = getString(R.string.tips_pebble)
        val highLightStr = getString(R.string.meta_pebble)
        val normalStyle = Style(R.color.white, 35)
        val highlightStyle = Style(R.color.green_400, 35)
        mTvTips.renderHighlightTips(tipsStr, normalStyle, highLightStr, highlightStyle)
    }

    override fun onStart() {
        super.onStart()
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun initData(savedInstanceState: Bundle?) {
        mActivateVM.queryActivatedResult(mDevice?.imei ?: "")
        WalletConnector.init(::onConnected, ::onDisconnected, ::onOpenWallet, ::onConnectError)
        mTvActivate.setOnClickListener {
            if (WalletConnector.isConnected()) {
                startActivity<NftListActivity>()
            } else {
                WalletConnector.connect()
                mProgress.show()
            }
        }
        AppUtils.registerAppStatusChangedListener(object : Utils.OnAppStatusChangedListener {
            override fun onForeground(activity: Activity?) {
                if ( mNeedResponse) {
                    mNeedResponse = false
                    this@DevicePanelActivity.startActivity<NftListActivity>()
                }
            }

            override fun onBackground(activity: Activity?) {
            }

        })
    }

    private fun onConnected(address: String, chainId: Long) {
        mNeedResponse = true
    }

    private fun onDisconnected() {
        mNeedResponse = false
        ActivityUtils.getActivityList().forEach {
            if (it !is DevicePanelActivity) {
                it.onBackPressed()
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        ActivityUtils.getActivityList().forEach {
            if (it !is DevicePanelActivity) {
                it.onBackPressed()
            }
        }
    }

    private fun onOpenWallet() {
        mProgress.dismiss()
    }

    private fun onConnectError() {
        runOnUiThread {
            mProgress.dismiss()
            PromptDialog(this)
                .setTitle(getString(R.string.error))
                .setContent(getString(R.string.connect_error))
                .setPositiveButton(getString(R.string.ok))
                .show()
        }

    }

    override fun registerObserver() {
        mActivateVM.mIsActivatedLd.observe(this) {
            mIsActivated = it
            if (it) {
                mLlActivated.visible()
                mTvActivate.gone()
                mTvTips.invisible()
                mTvTitle.text = getString(R.string.meta_pebble)
                mDevice?.let { device ->
                    mPebbleVM.queryPebbleStatus(device.imei)
                }
            } else {
                mTvTitle.text = getString(R.string.device)
                mLlActivated.gone()
                mTvTips.visible()
                mTvActivate.visible()
            }
        }
        mPebbleVM.mDeviceStatusLD.observe(this) {
            mSwitch.isChecked = it
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        WalletConnector.disconnect()
    }
}
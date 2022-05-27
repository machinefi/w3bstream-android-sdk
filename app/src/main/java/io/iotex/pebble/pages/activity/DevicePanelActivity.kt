package io.iotex.pebble.pages.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.Utils
import io.iotex.core.base.BaseActivity
import io.iotex.pebble.R
import io.iotex.pebble.constant.PebbleStore
import io.iotex.pebble.module.db.AppDatabase
import io.iotex.pebble.module.db.entries.DEVICE_POWER_OFF
import io.iotex.pebble.module.db.entries.DEVICE_POWER_ON
import io.iotex.pebble.module.viewmodel.ActivateVM
import io.iotex.pebble.module.viewmodel.PebbleVM
import io.iotex.pebble.module.walletconnect.WcKit
import io.iotex.pebble.utils.DeviceHelper
import io.iotex.pebble.utils.extension.*
import io.iotex.pebble.widget.DeviceMenuDialog
import io.iotex.pebble.widget.PickerDialog
import io.iotex.pebble.widget.PromptDialog
import kotlinx.android.synthetic.main.activity_device_panel.*
import kotlinx.android.synthetic.main.include_bar.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity

class DevicePanelActivity : BaseActivity(R.layout.activity_device_panel) {

    private val mActivateVM by lazy {
        ViewModelProvider(this, mVmFactory)[ActivateVM::class.java]
    }

    private val mPebbleVM by lazy {
        ViewModelProvider(this, mVmFactory)[PebbleVM::class.java]
    }

    private val mDevice by lazy {
        PebbleStore.mDevice
    }

    private var mNeedResponse = false

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
            if (isChecked) {
                powerOn()
            } else {
                powerOff()
            }
        }
    }

    private fun renderMenu() {
        mIvMenu.setVisible()
        mIvMenu.setOnClickListener {
            DeviceMenuDialog()
                .setHistoryListener {
                    startActivity<HistoryActivity>()
                }
                .setOwnershipListener {
                    startActivity<OwnershipActivity>()
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

    private fun powerOn() {
        PermissionUtils
            .permission(PermissionConstants.LOCATION)
            .callback(object : PermissionUtils.SimpleCallback {
                override fun onGranted() {

                    mDevice?.let { device ->
                        DeviceHelper.powerOn(device)
                        device.power = DEVICE_POWER_ON

                        doAsync {
                            AppDatabase.mInstance.deviceDao().update(device)
                        }
                    }
                }

                override fun onDenied() {
                }
            })
            .request()
    }

    private fun powerOff() {
        mDevice?.let { device ->
            DeviceHelper.powerOff(device.imei)
            device.power = DEVICE_POWER_OFF

            doAsync {
                AppDatabase.mInstance.deviceDao().update(device)
            }
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        mActivateVM.queryActivatedResult(mDevice?.imei ?: "")
        mWcActivate.start(WcKit.mWalletConnectKit, ::onConnected, ::onDisconnected)
        mWcActivate.setOnClickListener {
            if (WcKit.isConnected()) {
                startActivity<NftListActivity>()
            } else {
                WcKit.connect(mWcActivate)
            }
        }
        AppUtils.registerAppStatusChangedListener(object : Utils.OnAppStatusChangedListener {
            override fun onForeground(activity: Activity?) {
                "onForeground".i()
                if (WcKit.isConnected() && mNeedResponse) {
                    mNeedResponse = false
                    this@DevicePanelActivity.startActivity<NftListActivity>()
                }
            }

            override fun onBackground(activity: Activity?) {
            }

        })

    }

    private fun onConnected(address: String) {
        mNeedResponse = true
    }

    private fun onDisconnected() {
        mNeedResponse = false
        WcKit.disconnect()
        startActivity(
            Intent(this, DevicePanelActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
    }

    override fun registerObserver() {
        mActivateVM.mIsActivatedLd.observe(this) {
//            if (it) {
//                mLlActivated.setVisible()
//                mFlConnect.setGone()
//                mDevice?.let { device ->
//                    mPebbleVM.queryPebbleStatus(device.imei)
//                }
//            } else {
//                mLlActivated.setGone()
//                mFlConnect.setVisible()
//            }
            mFlConnect.setVisible()
        }
        mPebbleVM.mDeviceStatusLD.observe(this) {
            mSwitch.isChecked = it
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        WcKit.disconnect()
    }
}
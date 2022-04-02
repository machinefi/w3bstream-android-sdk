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
import io.iotex.pebble.module.db.AppDatabase
import io.iotex.pebble.module.db.entries.*
import io.iotex.pebble.module.mqtt.EncryptUtil
import io.iotex.pebble.module.mqtt.MqttHelper
import io.iotex.pebble.module.viewmodel.WalletVM
import io.iotex.pebble.module.walletconnect.WcKit
import io.iotex.pebble.utils.DeviceHelper
import io.iotex.pebble.utils.extension.i
import io.iotex.pebble.utils.extension.renderHighlightTips
import io.iotex.pebble.utils.extension.setVisible
import io.iotex.pebble.widget.DeviceMenuDialog
import io.iotex.pebble.widget.PromptDialog
import kotlinx.android.synthetic.main.activity_device_panel.*
import kotlinx.android.synthetic.main.include_bar.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity

class DevicePanelActivity : BaseActivity(R.layout.activity_device_panel) {

    private val mWalletVM by lazy {
        ViewModelProvider(this)[WalletVM::class.java]
    }

    private var mDevice: DeviceEntry? = null

    private val mConfirmDialog by lazy {
        PromptDialog(this).setPositiveButton("Confirm") {
            confirm()
        }
    }

    private var mNeedResponse = false

    override fun initView(savedInstanceState: Bundle?) {
        mDevice = intent.getSerializableExtra(KEY_DEVICE) as? DeviceEntry
        renderMenu()
        renderTips()
    }

    private fun renderMenu() {
        mIvMenu.setVisible()
        mIvMenu.setOnClickListener {
            DeviceMenuDialog()
                .setHistoryListener {
                    startActivity<HistoryActivity>(
                        HistoryActivity.KEY_IMEI to mDevice?.imei
                    )
                }
                .setOwnershipListener {
                    startActivity<OwnershipActivity>(
                        OwnershipActivity.KEY_DEVICE to mDevice
                    )
                }
                .setAboutListener {
                    startActivity<AboutActivity>(
                        AboutActivity.KEY_DEVICE to mDevice
                    )
                }
                .setSettingListener {
                    startActivity<SettingActivity>(
                        SettingActivity.KEY_DEVICE to mDevice
                    )
                }
                .show(mIvMenu)
        }
    }

    private fun renderTips() {
        val tipsStr = getString(R.string.tips_pebble)
        val highLightStr = getString(R.string.meta_pebble)
        mTvTips.renderHighlightTips(tipsStr, highLightStr)
    }

    override fun onStart() {
        super.onStart()
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    private fun pressPowerOff() {
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

    private fun pressPowerOn() {

        mDevice?.let { device ->
            DeviceHelper.powerOff(device.imei)
            device.power = DEVICE_POWER_OFF

            doAsync {
                AppDatabase.mInstance.deviceDao().update(device)
            }
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
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
        "onConnected".i()
        mNeedResponse = true
    }

    private fun onDisconnected() {
        mNeedResponse = false
        WcKit.disconnect()
        startActivity(Intent(this, DevicePanelActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
    }

    private fun confirm() {
        mDevice?.let { device ->
            if (device.walletAddress.isBlank()) return
            doAsync {
                val data = EncryptUtil.signConfirm(device)
                MqttHelper.publishConfirm(device.imei, data)
            }
        }
    }

    override fun registerObserver() {
        mWalletVM.mDeviceUpdateLiveData.observe(this) {
            if (it != null) {
                mDevice = it
                if (it.status == DEVICE_STATUS_PROPOSE) {
                    mConfirmDialog.show()
                }
                if (it.status == DEVICE_STATUS_CONFIRM) {
                    DeviceHelper.pollingSendData(it)
                    DeviceHelper.stopQuerying()
                } else {
                    DeviceHelper.stopSendingData()
                    DeviceHelper.pollingQuery(it)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        WcKit.disconnect()
        "onDestroy".i()
    }

    companion object {
        const val KEY_DEVICE = "key_device"
    }
}
package io.iotex.pebble.pages

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils
import io.iotex.core.base.BaseActivity
import io.iotex.pebble.R
import io.iotex.pebble.module.db.AppDatabase
import io.iotex.pebble.module.db.entries.*
import io.iotex.pebble.module.mqtt.EncryptUtil
import io.iotex.pebble.module.mqtt.MqttHelper
import io.iotex.pebble.module.viewmodel.WalletVM
import io.iotex.pebble.utils.DeviceHelper
import io.iotex.pebble.utils.extension.setGone
import io.iotex.pebble.utils.extension.setVisible
import io.iotex.pebble.widget.PromptDialog
import io.iotex.pebble.widget.DeviceMenuDialog
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

    override fun initView(savedInstanceState: Bundle?) {
        mDevice = intent.getSerializableExtra(KEY_DEVICE) as? DeviceEntry
        mIvMenu.setVisible()
        mIvMenu.setOnClickListener {
            DeviceMenuDialog()
                .setHistoryListener {
                    startActivity<HistoryActivity>(
                        HistoryActivity.KEY_IMEI to mDevice?.imei
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

        mDevice?.let {
            showStatus(it.status)
        }

        mIvPowerOff.setOnClickListener {
            pressPowerOff()
        }

//        mIvPowerOn.setOnClickListener {
//            pressPowerOn()
//        }
    }

    override fun onStart() {
        super.onStart()
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    private fun showStatus(status: Int) {
        val statusText = when(status) {
            DEVICE_STATUS_UNREGISTER -> getString(R.string.unregister)
            DEVICE_STATUS_PROPOSE -> getString(R.string.propose)
            DEVICE_STATUS_CONFIRM -> getString(R.string.confirmed)
            else -> getString(R.string.unregister)
        }
//        mTvStatus.text = statusText
    }

    private fun pressPowerOff() {
        PermissionUtils
            .permission(PermissionConstants.LOCATION)
            .callback(object : PermissionUtils.SimpleCallback {
                override fun onGranted() {
//                    mIvDeviceOff.setGone()
                    mIvPowerOff.setGone()
//                    mIvDeviceOn.setVisible()
//                    mIvPowerOn.setVisible()

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
//        mIvDeviceOff.setVisible()
        mIvPowerOff.setVisible()
//        mIvDeviceOn.setGone()
//        mIvPowerOn.setGone()

        mDevice?.let { device ->
            DeviceHelper.powerOff(device.imei)
            device.power = DEVICE_POWER_OFF

            doAsync {
                AppDatabase.mInstance.deviceDao().update(device)
            }
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        mDevice?.let {
            if (it.power == DEVICE_POWER_ON) {
                pressPowerOff()
            }
        }
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
                showStatus(it.status)
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

    companion object {
        const val KEY_DEVICE = "key_device"
    }
}
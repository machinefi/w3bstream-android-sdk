package com.machinefi.metapebble.pages.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.blankj.utilcode.util.RegexUtils
import com.blankj.utilcode.util.SPUtils
import com.machinefi.core.base.BaseActivity
import com.machinefi.metapebble.R
import com.machinefi.metapebble.constant.*
import com.machinefi.metapebble.module.manager.PebbleManager
import com.machinefi.metapebble.module.viewmodel.PebbleVM
import com.machinefi.metapebble.utils.GPS_PRECISION
import com.machinefi.metapebble.utils.INTERVAL_SEND_DATA
import com.machinefi.metapebble.utils.extension.gone
import com.machinefi.metapebble.utils.extension.visible
import com.machinefi.metapebble.widget.PickerDialog
import com.machinefi.metapebble.widget.PickerItemData
import com.machinefi.metapebble.widget.ServerDialog
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : BaseActivity(R.layout.activity_setting) {

    private val mPebbleVM by lazy {
        ViewModelProvider(this, mVmFactory)[PebbleVM::class.java]
    }

    private val mDevice by lazy {
        PebbleStore.mDevice
    }

    private val mSubmitFrequencyList = listOf(
        PickerItemData("1 min", 1),
        PickerItemData("2 mins", 2),
        PickerItemData("3 mins", 3),
        PickerItemData("4 mins", 4),
        PickerItemData("5 mins", 5),
        PickerItemData("6 mins", 6),
        PickerItemData("7 mins", 7),
        PickerItemData("8 mins", 8),
        PickerItemData("9 mins", 9),
        PickerItemData("10 mins", 10),
    )

    private val mGpsPrecisionList = listOf(
        PickerItemData("Fine (~100M)", 100),
        PickerItemData("Medium (~1KM)", 1000),
        PickerItemData("Coarse (~10KM)", 10000)
    )

    override fun initView(savedInstanceState: Bundle?) {
        mSbGps.isChecked = SPUtils.getInstance().getBoolean(SP_KEY_GPS_CHECKED, true)
        mSbGps.setOnCheckedChangeListener { view, isChecked ->
            SPUtils.getInstance().put(SP_KEY_GPS_CHECKED, isChecked)
        }

        getFrequencyCurrentItem()?.also {
            mTvFrequency.text = it.label
        }
        mRlFrequency.setOnClickListener {
            val picker = PickerDialog(this)

            val curItem = getFrequencyCurrentItem()
            if (curItem != null) {
                picker.setCurrentItem(curItem)
            }
            picker.setTitle(getString(R.string.gps_collection_interval))
                .setOptions(mSubmitFrequencyList)
                .setPositiveButton(getString(R.string.confirm)) {
                    mTvFrequency.text = it.label
                    PebbleManager.pebbleKit.uploadFrequency(it.value)
                    SPUtils.getInstance().put(SP_KEY_SUBMIT_FREQUENCY, it.value)
                    mDevice?.imei?.let {
                        mPebbleVM.resumeUploading(it)
                    }
                }
                .show()
        }

        getPrecisionCurrentItem()?.let {
            mTvGpsPrecision.text = it.label
        }
        mRlGpsPrecision.setOnClickListener {
            val picker = PickerDialog(this)

            val curItem = getPrecisionCurrentItem()
            if (curItem != null) {
                picker.setCurrentItem(curItem)
            }
            picker.setTitle(getString(R.string.gps_precision))
                .setOptions(mGpsPrecisionList)
                .setPositiveButton(getString(R.string.confirm)) {
                    mTvGpsPrecision.text = it.label
                    SPUtils.getInstance().put(SP_KEY_GPS_PRECISION, it.value)
                }
                .show()
        }

        initServer()
    }

    private fun initServer() {
        mSbServer.isChecked = SPUtils.getInstance().getBoolean(SP_KEY_SERVER_CHECKED, true)
        mSbServer.setOnCheckedChangeListener { view, isChecked ->
            SPUtils.getInstance().put(SP_KEY_SERVER_CHECKED, isChecked)
            if (!isChecked) {
                mLlHttpsPreview.visible()
                mLlServerInput.gone()
                mIvHttpsReset.gone()

                mLlSocketPreview.visible()
                mLlSocketInput.gone()
                mIvSocketReset.gone()
            } else {
                mIvHttpsReset.visible()
                mIvSocketReset.visible()
            }
        }

        val httpsUrl = SPUtils.getInstance().getString(SP_KEY_HTTPS_URL, URL_HTTPS_SERVER)
        mTvHttpsPreview.text = httpsUrl
        mEtHttps.setText(httpsUrl)
        mIvHttpsReset.setOnClickListener {
            if (!mSbServer.isChecked) return@setOnClickListener
            mLlHttpsPreview.gone()
            mLlServerInput.visible()
        }
        mLlHttpsRefresh.setOnClickListener {
            val url = mEtHttps.text.trim().toString()
            if (RegexUtils.isURL(url)) {
                mTvHttpsError.gone()
                mTvHttpsPreview.text = url
                SPUtils.getInstance().put(SP_KEY_HTTPS_URL, url)
                mLlHttpsPreview.visible()
                mLlServerInput.gone()
            } else {
                mTvHttpsError.visible()
            }
        }

        val socketUrl = SPUtils.getInstance().getString(SP_KEY_SOCKET_URL, URL_SOCKET_SERVER)
        mTvSocketPreview.text = socketUrl
        mEtSocket.setText(socketUrl)
        mIvSocketReset.setOnClickListener {
            if (!mSbServer.isChecked) return@setOnClickListener
            mLlSocketPreview.gone()
            mLlSocketInput.visible()
        }
        mLlSocketRefresh.setOnClickListener {
            val url = mEtSocket.text.trim().toString()
            if (RegexUtils.isURL(url)) {
                mTvSocketError.gone()
                mTvSocketPreview.text = url
                SPUtils.getInstance().put(SP_KEY_SOCKET_URL, url)
                mLlSocketPreview.visible()
                mLlSocketInput.gone()
            } else {
                mTvSocketError.visible()
            }
        }
    }

    private fun getFrequencyCurrentItem(): PickerItemData? {
        return mSubmitFrequencyList.firstOrNull {
            it.value == SPUtils.getInstance().getInt(SP_KEY_SUBMIT_FREQUENCY, INTERVAL_SEND_DATA)
        }
    }

    private fun getPrecisionCurrentItem(): PickerItemData? {
        return mGpsPrecisionList.firstOrNull {
            it.value == SPUtils.getInstance().getInt(SP_KEY_GPS_PRECISION, GPS_PRECISION)
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
    }

    override fun registerObserver() {
    }
}
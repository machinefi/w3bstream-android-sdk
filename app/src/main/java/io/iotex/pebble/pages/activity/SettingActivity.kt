package io.iotex.pebble.pages.activity

import android.os.Bundle
import com.blankj.utilcode.util.SPUtils
import io.iotex.core.base.BaseActivity
import io.iotex.pebble.R
import io.iotex.pebble.constant.PebbleStore
import io.iotex.pebble.constant.SP_KEY_GPS_CHECKED
import io.iotex.pebble.constant.SP_KEY_GPS_PRECISION
import io.iotex.pebble.constant.SP_KEY_SUBMIT_FREQUENCY
import io.iotex.pebble.module.db.entries.DeviceEntry
import io.iotex.pebble.utils.DeviceHelper
import io.iotex.pebble.utils.GPS_PRECISION
import io.iotex.pebble.utils.INTERVAL_SEND_DATA
import io.iotex.pebble.utils.getPickerBuilder
import io.iotex.pebble.widget.PickerDialog
import io.iotex.pebble.widget.PickerItemData
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : BaseActivity(R.layout.activity_setting) {

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
        PickerItemData("100 m", 100),
        PickerItemData("1 km", 1000),
        PickerItemData("10 km", 10000)
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
            picker.setTitle(getString(R.string.submit_frequency))
                .setOptions(mSubmitFrequencyList)
                .setPositiveButton(getString(R.string.confirm)) {
                    mTvFrequency.text = it.label
                    SPUtils.getInstance().put(SP_KEY_SUBMIT_FREQUENCY, it.value)
                    mDevice?.let { device ->
                        DeviceHelper.pollingSendData(device)
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
                    mDevice?.let { device ->
                        DeviceHelper.pollingSendData(device)
                    }
                }
                .show()
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
package io.iotex.pebble.pages

import android.os.Bundle
import com.blankj.utilcode.util.SPUtils
import io.iotex.core.base.BaseActivity
import io.iotex.pebble.R
import io.iotex.pebble.constant.SP_KEY_GPS_CHECKED
import io.iotex.pebble.constant.SP_KEY_GPS_PRECISION
import io.iotex.pebble.constant.SP_KEY_SUBMIT_FREQUENCY
import io.iotex.pebble.module.db.entries.DeviceEntry
import io.iotex.pebble.utils.DeviceHelper
import io.iotex.pebble.utils.GPS_PRECISION
import io.iotex.pebble.utils.INTERVAL_SEND_DATA
import io.iotex.pebble.utils.getPickerBuilder
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity: BaseActivity(R.layout.activity_setting) {

    private val mDevice by lazy {
        intent.getSerializableExtra(AboutActivity.KEY_DEVICE) as? DeviceEntry
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
        mRlFrequency.setOnClickListener {
            val index = mSubmitFrequencyList.indexOfFirst {
                it.value == SPUtils.getInstance().getInt(SP_KEY_SUBMIT_FREQUENCY, INTERVAL_SEND_DATA)
            }
            val builder = getPickerBuilder<String>(this, getString(R.string.submit_frequency), index
            ) { options1, options2, options3, v ->
                mTvFrequency.text = mSubmitFrequencyList[options1].label
                SPUtils.getInstance().put(SP_KEY_SUBMIT_FREQUENCY, mSubmitFrequencyList[options1].value)
                mDevice?.let {
                    DeviceHelper.pollingSendData(it)
                }
            }
            builder.setPicker(mSubmitFrequencyList.map { it.label })
            builder.show()
        }
        val defFrequencyItem = mSubmitFrequencyList.firstOrNull {
            it.value == SPUtils.getInstance().getInt(SP_KEY_SUBMIT_FREQUENCY, INTERVAL_SEND_DATA)
        }
        defFrequencyItem?.let {
            mTvFrequency.text = it.label
        }

        mRlGpsPrecision.setOnClickListener {
            val index = mGpsPrecisionList.indexOfFirst {
                it.value == SPUtils.getInstance().getInt(SP_KEY_GPS_PRECISION, GPS_PRECISION)
            }
            val builder = getPickerBuilder<String>(this, getString(R.string.gps_precision), index
            ) { options1, options2, options3, v ->
                mTvGpsPrecision.text = mGpsPrecisionList[options1].label
                SPUtils.getInstance().put(SP_KEY_GPS_PRECISION, mGpsPrecisionList[options1].value)
            }
            builder.setPicker(mGpsPrecisionList.map { it.label })
            builder.show()
        }
        val defGpsPrecisionItem = mGpsPrecisionList.firstOrNull {
            it.value == SPUtils.getInstance().getInt(SP_KEY_GPS_PRECISION, GPS_PRECISION)
        }
        defGpsPrecisionItem?.let {
            mTvGpsPrecision.text = it.label
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
    }

    override fun registerObserver() {
    }

    companion object {
        const val KEY_DEVICE = "key_device"
    }

}

data class PickerItemData(val label: String, val value: Int)
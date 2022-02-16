package io.iotex.pebble.module.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import io.iotex.core.base.BaseViewModel
import io.iotex.pebble.app.GlobalPreferences
import io.iotex.pebble.constant.UpdateDeviceEvent
import io.iotex.pebble.module.db.AppDatabase
import io.iotex.pebble.module.db.entries.DeviceEntry
import io.iotex.pebble.module.db.entries.RecordEntry
import io.iotex.pebble.utils.extension.i
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class WalletVM @Inject constructor() : BaseViewModel() {

    val mDeviceListLiveData = MutableLiveData<List<DeviceEntry>>()
    val mDeviceUpdateLiveData = MutableLiveData<DeviceEntry>()
    val mRecordListLiveData = MutableLiveData<List<RecordEntry>>()

    override fun useEventBus(): Boolean {
        return true
    }

    @SuppressLint("CheckResult")
    fun queryDeviceList() {
        AppDatabase.mInstance.deviceDao()
            .queryAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ data ->
                mDeviceListLiveData.postValue(data)
            }, {
                mDeviceListLiveData.postValue(null)
            })
    }


    @SuppressLint("CheckResult")
    fun queryRecordList(imei: String, page: Int, pageSize: Int) {
        AppDatabase.mInstance.recordDao()
            .queryByImei(imei, page, pageSize)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ data ->
                mRecordListLiveData.postValue(data)
            }, {
                mRecordListLiveData.postValue(null)
            })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUpdateDeviceEvent(event: UpdateDeviceEvent) {
        mDeviceUpdateLiveData.postValue(event.device)
        "onUpdateDeviceEvent ${event.device.status}".i()
    }

}
package com.machinefi.metapebble.module.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils
import com.machinefi.core.base.BaseViewModel
import com.machinefi.metapebble.module.db.entries.DEVICE_POWER_OFF
import com.machinefi.metapebble.module.db.entries.DEVICE_POWER_ON
import com.machinefi.metapebble.module.db.entries.DeviceEntry
import com.machinefi.metapebble.module.repository.PebbleRepo
import com.machinefi.metapebble.module.repository.UploadRepo
import com.machinefi.metapebble.pages.binder.NftEntry
import com.machinefi.metapebble.utils.extension.e
import io.iotex.graphql.test.RecordQuery
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class PebbleVM @Inject constructor(val mPebbleRepo: PebbleRepo, val mUploadRepo: UploadRepo) : BaseViewModel() {

    val mDeviceListLD = MutableLiveData<List<DeviceEntry>>()
    val mRecordListLD = MutableLiveData<List<RecordQuery.Pebble_device_record>>()
    val mNftListLD = MutableLiveData<List<NftEntry>>()
    val mDeviceStatusLD = MutableLiveData<Boolean>()

    fun queryDeviceList() {
        viewModelScope.launch {
            val data = mPebbleRepo.queryDeviceList()
            mDeviceListLD.postValue(data)
        }
    }

    fun queryRecordList(imei: String, page: Int, pageSize: Int) {
        viewModelScope.launch {
            val data = mPebbleRepo.queryRecordList(imei, page, pageSize)
            mRecordListLD.postValue(data)
        }
    }

    fun queryPebbleStatus(imei: String) {
        viewModelScope.launch {
            val device = mPebbleRepo.queryPebbleStatus(imei)
            if (device?.power == DEVICE_POWER_ON) {
                mDeviceStatusLD.postValue(true)
            } else {
                mDeviceStatusLD.postValue(false)
            }
        }
    }

    fun queryNftList(address: String) {
        val errorHandler = CoroutineExceptionHandler { _, exception ->
            exception.message?.e()
            mNftListLD.postValue(null)
        }
        viewModelScope.launch(errorHandler) {
            val nftList = mPebbleRepo.queryNftList(address)
            if (!nftList?.result?.get(0)?.tokenList.isNullOrEmpty()) {
                nftList?.result?.get(0)?.tokenList?.filterNotNull()?.map { token ->
                    NftEntry(token, nftList.result[0]?.address ?: "")
                }?.also { list ->
                    val consumed = list.filter { it.nft.consumed == true }
                    val unconsumed = list.filter { it.nft.consumed == false }
                    mNftListLD.postValue(unconsumed.plus(consumed))
                    return@launch
                }
            }
            mNftListLD.postValue(null)
        }
    }

    fun powerOn(device: DeviceEntry) {
        PermissionUtils
            .permission(PermissionConstants.LOCATION)
            .callback(object : PermissionUtils.SimpleCallback {
                override fun onGranted() {
                    uploadData(device)
                }

                override fun onDenied() {
                }
            })
            .request()
    }

    fun powerOff(device: DeviceEntry) {
        viewModelScope.launch {
            mUploadRepo.stopUploadMetadata()
            device.power = DEVICE_POWER_OFF
            mPebbleRepo.updateDevice(device)
        }
    }

    private fun uploadData(device: DeviceEntry) {
        viewModelScope.launch {
            mUploadRepo.startUploadMetadata()
            device.power = DEVICE_POWER_ON
            mPebbleRepo.updateDevice(device)
        }
    }

    fun resumeUploading() {
        mUploadRepo.stopUploadMetadata()
        mUploadRepo.startUploadMetadata()
    }
}
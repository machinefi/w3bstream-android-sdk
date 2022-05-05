package io.iotex.pebble.module.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.iotex.core.base.BaseViewModel
import io.iotex.pebble.module.db.entries.DeviceEntry
import io.iotex.pebble.module.db.entries.RecordEntry
import io.iotex.pebble.module.repository.PebbleRepo
import io.iotex.pebble.pages.binder.NftEntry
import io.iotex.pebble.utils.extension.e
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class PebbleVM @Inject constructor(val mPebbleRepo: PebbleRepo) : BaseViewModel() {

    val mDeviceListLD = MutableLiveData<List<DeviceEntry>>()
    val mRecordListLD = MutableLiveData<List<RecordEntry>>()
    val mNftListLD = MutableLiveData<List<NftEntry>>()

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
                    mNftListLD.postValue(list)
                    return@launch
                }
            }
            mNftListLD.postValue(null)
        }
    }
}
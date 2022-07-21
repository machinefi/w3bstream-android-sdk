package com.machinefi.metapebble.module.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.machinefi.metapebble.utils.KeystoreUtil
import com.machinefi.core.base.BaseViewModel
import com.machinefi.metapebble.constant.CONTRACT_KEY_NFT
import com.machinefi.metapebble.constant.CONTRACT_KEY_REGISTER
import com.machinefi.metapebble.constant.PebbleStore
import com.machinefi.metapebble.constant.QueryActivateResultEvent
import com.machinefi.metapebble.module.db.AppDatabase
import com.machinefi.metapebble.module.db.entries.DeviceEntry
import com.machinefi.metapebble.module.manager.PebbleManager
import com.machinefi.metapebble.module.repository.ActivateRepo
import com.machinefi.metapebble.module.repository.AppRepo
import com.machinefi.metapebble.module.walletconnect.FunctionSignData
import com.machinefi.metapebble.module.walletconnect.WalletConnector
import com.machinefi.metapebble.utils.extension.e
import com.machinefi.metapebble.utils.extension.toHexByteArray
import com.machinefi.pebblekit.common.request.SignPebbleResult
import kotlinx.coroutines.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.web3j.abi.TypeEncoder
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.utils.Numeric
import javax.inject.Inject

class ActivateVM @Inject constructor(
    val mActivateRepo: ActivateRepo,
    val mAppRepo: AppRepo,
) : BaseViewModel() {

    val mSignDeviceLD = MutableLiveData<SignPebbleResult>()
    val mIsActivatedLd = MutableLiveData<Boolean>()
    val mApproveLd = MutableLiveData<String>()
    val mActivateLd = MutableLiveData<String>()

    override fun useEventBus() = true

    fun signDevice(device: DeviceEntry) {
        val errorHandler = CoroutineExceptionHandler { _, exception ->
            exception.message?.e()
            mSignDeviceLD.postValue(null)
        }
        viewModelScope.launch(errorHandler) {
            val result = PebbleManager.pebbleKit.sign(device.imei, device.sn, device.pubKey)
            mSignDeviceLD.postValue(result)
        }
    }

    fun approveRegistration(tokenId: String) {
        viewModelScope.launch {
            val registerContract = mAppRepo.queryContractByName(CONTRACT_KEY_REGISTER)?.address ?: return@launch
            val nftContract = mAppRepo.queryContractByName(CONTRACT_KEY_NFT)?.address ?: return@launch
            val signData = FunctionSignData.getApproveRegistrationDate(registerContract, tokenId)

            val response = WalletConnector.sendTransaction(
                nftContract, "0", signData
            )

            if (!response.result?.toString().isNullOrBlank()) {
                mApproveLd.postValue(tokenId)
            } else {
                mApproveLd.postValue(null)
            }
        }
    }

    fun activateMetaPebble(
        tokenId: String,
        pubKey: String,
        imei: String,
        sn: String,
        timestamp: String,
        authentication: String,
    ) {
        viewModelScope.launch {
            val walletAddress = WalletConnector.walletAddress ?: return@launch
            val registerContract = mAppRepo.queryContractByName(CONTRACT_KEY_REGISTER)?.address ?: return@launch
            val msg = Numeric.prependHexPrefix(
                TypeEncoder.encodePacked(Address(walletAddress)) +
                        TypeEncoder.encodePacked(Uint256(timestamp.toBigInteger()))
            )
            val signature = KeystoreUtil.signData(msg.toHexByteArray())

            val signData = FunctionSignData.getRegistrationData(
                tokenId,
                imei,
                pubKey,
                sn,
                timestamp,
                signature,
                authentication
            )
            val response = WalletConnector.sendTransaction(
                registerContract, "0", signData
            )

            if (!response.result?.toString().isNullOrBlank()) {
                mActivateLd.postValue(tokenId)
            } else {
                mActivateLd.postValue(null)
            }
        }
    }

    fun queryActivatedResult(imei: String) {
        val errorHandler = CoroutineExceptionHandler { _, exception ->
            exception.message?.e()
            mIsActivatedLd.postValue(false)
        }
        viewModelScope.launch(errorHandler) {
            val device = withContext(Dispatchers.IO) {
                AppDatabase.mInstance.deviceDao().queryByImei(imei)
            }
            if (!device?.owner.isNullOrBlank()) {
                mIsActivatedLd.postValue(true)
            } else {
                val valid = mActivateRepo.queryActivatedResult(imei)
                mIsActivatedLd.postValue(valid)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onQueryActivateResultEvent(event: QueryActivateResultEvent) {
        PebbleStore.mDevice?.imei?.let {
            queryActivatedResult(it)
        }
    }

}
package io.iotex.pebble.module.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.iotex.core.base.BaseViewModel
import io.iotex.pebble.constant.NFT_CONTRACT
import io.iotex.pebble.constant.PebbleStore
import io.iotex.pebble.constant.QueryActivateResultEvent
import io.iotex.pebble.constant.REGISTRATION_CONTRACT
import io.iotex.pebble.module.db.AppDatabase
import io.iotex.pebble.module.db.entries.DeviceEntry
import io.iotex.pebble.module.http.ErrorHandleSubscriber
import io.iotex.pebble.module.http.SignPebbleBody
import io.iotex.pebble.module.http.SignPebbleResp
import io.iotex.pebble.module.repository.ActivateRepo
import io.iotex.pebble.module.walletconnect.FunctionSignData
import io.iotex.pebble.module.walletconnect.WcKit
import io.iotex.pebble.utils.extension.e
import io.iotex.pebble.utils.extension.i
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.web3j.abi.TypeEncoder
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.DynamicArray
import org.web3j.abi.datatypes.DynamicStruct
import org.web3j.abi.datatypes.StaticArray
import org.web3j.abi.datatypes.generated.Int256
import org.web3j.abi.datatypes.generated.Int32
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.utils.Numeric
import java.math.BigInteger
import javax.inject.Inject

class ActivateVM @Inject constructor(val mActivateRepo: ActivateRepo) : BaseViewModel() {

    val mSignDeviceLD = MutableLiveData<SignPebbleResp>()
    val mIsActivatedLd = MutableLiveData<Boolean>()
    val mApproveLd = MutableLiveData<String>()
    val mActivateLd = MutableLiveData<String>()

    override fun useEventBus() = true

    fun signDevice(device: DeviceEntry) {
        val body = SignPebbleBody(device.imei, device.sn, device.pubKey)
        mActivateRepo.signDevice(body)
            .doOnSubscribe {
                addDisposable(it)
            }
            .subscribe(object : ErrorHandleSubscriber<SignPebbleResp>() {
                override fun onSuccess(t: SignPebbleResp) {
                    viewModelScope.launch {
                        mSignDeviceLD.postValue(t)
                    }
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    mSignDeviceLD.postValue(null)
                }
            })
    }

    fun approveRegistration(tokenId: String) {
        viewModelScope.launch {
            val signData =
                FunctionSignData.getApproveRegistrationDate(REGISTRATION_CONTRACT, tokenId)

            val map = mutableMapOf<String, String>().apply {
                this["from"] = WcKit.walletAddress() ?: ""
                this["to"] = NFT_CONTRACT
                this["data"] = signData
            }
            val response = mActivateRepo.executeContract(map)

            if (!response.result?.toString().isNullOrBlank()) {
                mApproveLd.postValue(tokenId)
            }
        }
    }

    fun activateMetaPebble(
        tokenId: String,
        pubKey: String,
        imei: String,
        sn: String,
        timestamp: String,
        authentication: String
    ) {
        viewModelScope.launch {
            WcKit.walletAddress() ?: return@launch
            "Address : ${WcKit.walletAddress()}".i()
            val msg = Numeric.prependHexPrefix(TypeEncoder.encodePacked(Address(WcKit.walletAddress())) +
                    TypeEncoder.encodePacked(Uint256(timestamp.toBigInteger())))
            "msg : $msg".i()
            val rawSignature = mActivateRepo.signMessage(msg)
            "rawSignature : $rawSignature".i()
            val signature = mActivateRepo.generateSignature(rawSignature)
            "signature : $signature".i()

            val signData = FunctionSignData.getRegistrationData(
                tokenId,
                imei,
                pubKey,
                sn,
                timestamp,
                signature,
                authentication
            )
            "imei : $imei".i()
            "tokenId : $tokenId".i()
            "pubKey : $pubKey".i()
            "sn : $sn".i()
            "timestamp : $timestamp".i()
            "signData : $signData".i()
            "authentication : $authentication".i()
            val map = mutableMapOf<String, String>().apply {
                this["from"] = WcKit.walletAddress() ?: ""
                this["to"] = REGISTRATION_CONTRACT
                this["data"] = signData
            }
            val response = mActivateRepo.executeContract(map)

            if (!response.result?.toString().isNullOrBlank()) {
                mActivateLd.postValue(tokenId)
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
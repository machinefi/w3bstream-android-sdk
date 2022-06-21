package io.iotex.pebble.module.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.iotex.core.base.BaseViewModel
import io.iotex.pebble.constant.CONTRACT_KEY_NFT
import io.iotex.pebble.constant.CONTRACT_KEY_REGISTER
import io.iotex.pebble.constant.PebbleStore
import io.iotex.pebble.constant.QueryActivateResultEvent
import io.iotex.pebble.module.db.AppDatabase
import io.iotex.pebble.module.db.entries.DeviceEntry
import io.iotex.pebble.module.http.ErrorHandleSubscriber
import io.iotex.pebble.module.http.SignPebbleBody
import io.iotex.pebble.module.http.SignPebbleResp
import io.iotex.pebble.module.repository.ActivateRepo
import io.iotex.pebble.module.repository.AppRepo
import io.iotex.pebble.module.walletconnect.FunctionSignData
import io.iotex.pebble.module.walletconnect.WalletConnector
import io.iotex.pebble.utils.KeystoreUtil
import io.iotex.pebble.utils.extension.e
import io.iotex.pebble.utils.extension.toHexByteArray
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
            val walletAddress = WalletConnector.walletAddress ?: return@launch
            val registerContract = mAppRepo.queryContractByName(CONTRACT_KEY_REGISTER)?.address ?: return@launch
            val nftContract = mAppRepo.queryContractByName(CONTRACT_KEY_NFT)?.address ?: return@launch
            val signData = FunctionSignData.getApproveRegistrationDate(registerContract, tokenId)

            val params = mutableMapOf<String, String>().apply {
                this["from"] = walletAddress
                this["to"] = nftContract
                this["data"] = signData
            }
            val response = WalletConnector.signTransaction(listOf(params))

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
            val params = mutableMapOf<String, String>().apply {
                this["from"] = walletAddress
                this["to"] = registerContract
                this["data"] = signData
            }
            val response = WalletConnector.signTransaction(listOf(params))

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
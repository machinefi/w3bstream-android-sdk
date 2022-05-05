package io.iotex.pebble.module.repository

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import io.iotex.graphql.smartcontract.RegistrationResultQuery
import io.iotex.pebble.constant.CHAIN_ID
import io.iotex.pebble.constant.REGISTRATION_RESULT_CONTRACT
import io.iotex.pebble.di.annocation.ApolloClientSmartContract
import io.iotex.pebble.module.db.AppDatabase
import io.iotex.pebble.module.db.entries.DeviceEntry
import io.iotex.pebble.module.http.ApiService
import io.iotex.pebble.module.http.BaseResp
import io.iotex.pebble.module.http.SignPebbleBody
import io.iotex.pebble.module.http.SignPebbleResp
import io.iotex.pebble.utils.KeyStoreUtil
import io.iotex.pebble.utils.RxUtil
import io.iotex.pebble.utils.AddressUtil
import io.iotex.pebble.utils.extension.i
import io.iotex.pebble.utils.extension.toHexByteArray
import io.iotex.pebble.utils.extension.toHexString
import io.reactivex.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import wallet.core.jni.Curve
import wallet.core.jni.Hash
import wallet.core.jni.PrivateKey
import javax.inject.Inject

class ActivateRepo @Inject constructor(
    val mApiService: ApiService,
    val mPebbleRepo: PebbleRepo,
    @ApolloClientSmartContract val mApolloClient: ApolloClient
) {

    fun signDevice(request: SignPebbleBody): Observable<BaseResp<SignPebbleResp>> {
        return mApiService.signPebble(request).compose(RxUtil.observableSchedulers())
    }

    suspend fun signMessage(msg: String, device: DeviceEntry): String {
        return withContext(Dispatchers.IO) {
            val hash = Hash.keccak256(msg.toByteArray())
            val pk = KeyStoreUtil.resolvePrivateKey(device.password, device.hash)
            PrivateKey(pk.toHexByteArray()).sign(hash, Curve.SECP256K1).toHexString()
        }
    }

    suspend fun queryActivatedResult(imei: String) = withContext(Dispatchers.IO) {
        val contractOpt = Optional.presentIfNotNull(REGISTRATION_RESULT_CONTRACT)
        val chainIdOpt = Optional.presentIfNotNull(CHAIN_ID)
        val imeiOpt = Optional.presentIfNotNull(imei)
        val result = mApolloClient.query(RegistrationResultQuery(contractOpt, chainIdOpt, imeiOpt))
            .execute()
            .data?.result
        if (!result.isNullOrEmpty()) {
            val dataList = result[0]?.find?.split(",")
            if (!dataList.isNullOrEmpty()) {
                val owner = dataList[0]
                val activated = AddressUtil.isValidAddress(owner)
                if (activated) {
                    val device = AppDatabase.mInstance.deviceDao().queryByImei(imei)
                    device?.also {
                        device.owner = owner
                        mPebbleRepo.updateDevice(device)
                    }
                }
                return@withContext activated
            }
        }
        return@withContext false
    }

}
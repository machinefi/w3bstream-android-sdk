package io.iotex.pebble.module.repository

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import io.iotex.graphql.smartcontract.RegistrationResultQuery
import io.iotex.pebble.constant.CONTRACT_KEY_REGISTER_RESULT
import io.iotex.pebble.constant.PebbleStore
import io.iotex.pebble.di.annocation.ApolloClientSmartContract
import io.iotex.pebble.module.db.AppDatabase
import io.iotex.pebble.module.http.ApiService
import io.iotex.pebble.module.http.BaseResp
import io.iotex.pebble.module.http.SignPebbleBody
import io.iotex.pebble.module.http.SignPebbleResp
import io.iotex.pebble.utils.AddressUtil
import io.iotex.pebble.utils.RxUtil
import io.reactivex.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ActivateRepo @Inject constructor(
    val mApiService: ApiService,
    val mPebbleRepo: PebbleRepo,
    val mAppRepo: AppRepo,
    @ApolloClientSmartContract val mApolloClient: ApolloClient
) {

    fun signDevice(request: SignPebbleBody): Observable<BaseResp<SignPebbleResp>> {
        return mApiService.signPebble(request).compose(RxUtil.observableSchedulers())
    }

    suspend fun queryActivatedResult(imei: String) = withContext(Dispatchers.IO) {
        val contract = mAppRepo.queryContractByName(CONTRACT_KEY_REGISTER_RESULT) ?: return@withContext false
        val contractOpt = Optional.presentIfNotNull(contract.address)
        val chainIdOpt = Optional.presentIfNotNull(4690)
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
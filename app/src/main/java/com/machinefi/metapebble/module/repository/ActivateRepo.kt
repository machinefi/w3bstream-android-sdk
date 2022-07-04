package com.machinefi.metapebble.module.repository

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.machinefi.metapebble.constant.CONTRACT_KEY_REGISTER_RESULT
import com.machinefi.metapebble.di.annocation.ApolloClientSmartContract
import com.machinefi.metapebble.module.db.AppDatabase
import com.machinefi.metapebble.utils.AddressUtil
import io.iotex.graphql.smartcontract.RegistrationResultQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ActivateRepo @Inject constructor(
    val mPebbleRepo: PebbleRepo,
    val mAppRepo: AppRepo,
    @ApolloClientSmartContract val mApolloClient: ApolloClient
) {

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
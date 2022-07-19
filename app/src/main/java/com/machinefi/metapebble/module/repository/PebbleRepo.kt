package com.machinefi.metapebble.module.repository

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.machinefi.metapebble.constant.CONTRACT_KEY_NFT
import com.machinefi.metapebble.constant.PebbleStore
import com.machinefi.metapebble.di.annocation.ApolloClientSmartContract
import com.machinefi.metapebble.di.annocation.ApolloClientTest
import com.machinefi.metapebble.module.db.AppDatabase
import com.machinefi.metapebble.module.db.entries.DeviceEntry
import com.machinefi.metapebble.module.walletconnect.WalletConnector
import com.machinefi.metapebble.utils.AddressUtil
import com.machinefi.metapebble.utils.extension.i
import io.iotex.graphql.smartcontract.NftListQuery
import io.iotex.graphql.test.RecordQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PebbleRepo @Inject constructor(
    @ApolloClientSmartContract val mApolloClient: ApolloClient,
    @ApolloClientTest val mTestApolloClient: ApolloClient,
    val mAppRepo: AppRepo,
) {

    suspend fun queryDeviceList() = withContext(Dispatchers.IO) {
        AppDatabase.mInstance.deviceDao().queryAll()
    }

    suspend fun updateDevice(device: DeviceEntry) = withContext(Dispatchers.IO) {
        AppDatabase.mInstance.deviceDao().update(device)
        AppDatabase.mInstance.deviceDao()
            .queryByImei(device.imei)
            ?.also {
                PebbleStore.setDevice(it)
            }
    }

    suspend fun queryRecordList(imei: String, page: Int, pageSize: Int) =
        withContext(Dispatchers.IO) {
            AppDatabase.mInstance.recordDao().queryByImei(imei, page, pageSize)
        }

    suspend fun queryPebbleStatus(imei: String) =
        withContext(Dispatchers.IO) {
            AppDatabase.mInstance.deviceDao().queryByImei(imei)
        }

    suspend fun queryNftList(address: String) = withContext(Dispatchers.IO) {
        kotlin.runCatching {
            val contract = mAppRepo.queryContractByName(CONTRACT_KEY_NFT)?.address
                ?: return@withContext null
            val addressOpt = AddressUtil.convertWeb3Address(address)
            val contractOpt = Optional.presentIfNotNull(contract)
            WalletConnector.chainId ?: throw Exception("ChainId cannot be null")
            val chainIdOpt = Optional.presentIfNotNull(WalletConnector.chainId!!.toInt())
            mApolloClient.query(NftListQuery(addressOpt, contractOpt, chainIdOpt)).execute().data
        }.getOrNull()
    }

}
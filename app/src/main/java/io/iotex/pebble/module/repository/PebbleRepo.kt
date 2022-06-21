package io.iotex.pebble.module.repository

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import io.iotex.graphql.smartcontract.NftListQuery
import io.iotex.pebble.constant.CONTRACT_KEY_NFT
import io.iotex.pebble.constant.PebbleStore
import io.iotex.pebble.di.annocation.ApolloClientSmartContract
import io.iotex.pebble.module.db.AppDatabase
import io.iotex.pebble.module.db.entries.DeviceEntry
import io.iotex.pebble.module.walletconnect.WalletConnector
import io.iotex.pebble.pages.binder.NftEntry
import io.iotex.pebble.utils.AddressUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PebbleRepo @Inject constructor(
    @ApolloClientSmartContract val mApolloClient: ApolloClient,
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
        val contract = mAppRepo.queryContractByName(CONTRACT_KEY_NFT)?.address
            ?: return@withContext null
        val addressOpt = AddressUtil.convertWeb3Address(address)
        val contractOpt = Optional.presentIfNotNull(contract)
        WalletConnector.chainId ?: throw Exception("ChainId cannot be null")
        val chainIdOpt = Optional.presentIfNotNull(WalletConnector.chainId!!.toInt())
        mApolloClient.query(NftListQuery(addressOpt, contractOpt, chainIdOpt)).execute().data
    }

}
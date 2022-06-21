package io.iotex.pebble.module.repository

import com.apollographql.apollo3.ApolloClient
import io.iotex.graphql.metapebble.ContractQuery
import io.iotex.graphql.metapebble.VersionQuery
import io.iotex.pebble.di.annocation.ApolloClientMetaPebble
import io.iotex.pebble.module.db.AppDatabase
import io.iotex.pebble.module.db.entries.ContractEntry
import io.iotex.pebble.utils.extension.i
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AppRepo @Inject constructor(@ApolloClientMetaPebble val mApolloClient: ApolloClient) {

    suspend fun queryVersion(): VersionQuery.MetaPebble_version_android? {
        val versionList = mApolloClient.query(VersionQuery()).execute().data
        if (versionList?.metaPebble_version_android?.isNotEmpty() == true) {
            return versionList.metaPebble_version_android[0]
        }
        return null
    }

    private suspend fun queryContracts(): List<ContractEntry> {
        val contracts = AppDatabase.mInstance.contractDao().queryAll()
        if (contracts.isEmpty()) {
            queryContractsFromRemote()
        }
        return contracts
    }

    suspend fun queryContractsFromRemote(): List<ContractEntry> = withContext(Dispatchers.IO) {
        val remoteContracts = mApolloClient.query(ContractQuery()).execute().data
            ?.metaPebble_pebble_contract

        return@withContext remoteContracts?.map {
            ContractEntry(it.address, it.name, it.abi).also { contract ->
                AppDatabase.mInstance.contractDao().insertIfNonExist(contract)
            }
        } ?: emptyList()
    }

    suspend fun queryContractByName(name: String): ContractEntry? = withContext(Dispatchers.IO) {
        val contract = AppDatabase.mInstance.contractDao().queryContractByName(name)
        if (contract == null) {
            val contracts = queryContracts()
            return@withContext contracts.firstOrNull {
                it.name == name
            }
        }
        return@withContext contract
    }

}
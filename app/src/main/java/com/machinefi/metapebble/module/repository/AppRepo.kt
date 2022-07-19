package com.machinefi.metapebble.module.repository

import com.apollographql.apollo3.ApolloClient
import com.machinefi.metapebble.di.annocation.ApolloClientMetaPebble
import com.machinefi.metapebble.module.db.AppDatabase
import com.machinefi.metapebble.module.db.entries.ContractEntry
import io.iotex.graphql.metapebble.ContractQuery
import io.iotex.graphql.metapebble.VersionQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AppRepo @Inject constructor(@ApolloClientMetaPebble val mApolloClient: ApolloClient) {

    suspend fun queryVersion(): VersionQuery.MetaPebble_version_android? =
        withContext(Dispatchers.IO) {
            kotlin.runCatching {
                val version = mApolloClient.query(VersionQuery()).execute().data
                if (version?.metaPebble_version_android?.isNotEmpty() == true) {
                    return@withContext version.metaPebble_version_android[0]
                }
            }
            return@withContext null
        }

    private suspend fun queryContracts(): List<ContractEntry> {
        val contracts = AppDatabase.mInstance.contractDao().queryAll()
        if (contracts.isEmpty()) {
            queryContractsFromRemote()
        }
        return contracts
    }

    suspend fun queryContractsFromRemote(): List<ContractEntry> = withContext(Dispatchers.IO) {
        kotlin.runCatching {
            val remoteContracts = mApolloClient.query(ContractQuery()).execute().data
                ?.metaPebble_pebble_contract
            remoteContracts?.map {
                ContractEntry(it.address, it.name, it.abi).also { contract ->
                    AppDatabase.mInstance.contractDao().insertIfNonExist(contract)
                }
            }
        }.getOrNull() ?: emptyList()
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
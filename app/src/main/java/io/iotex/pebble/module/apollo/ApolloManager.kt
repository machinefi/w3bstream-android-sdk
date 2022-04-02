package io.iotex.pebble.module.apollo

import com.apollographql.apollo3.ApolloClient
import io.iotex.VersionQuery

object ApolloManager {

    private val mApolloClient by lazy {
        ApolloClient.Builder()
            .serverUrl("https://iopay-api.iotex.io/v1/graphql")
            .build()
    }

    suspend fun queryVersion(): VersionQuery.Metapebble_v_ctrl_android? {
        val versionList = mApolloClient.query(VersionQuery()).execute().data
        if (versionList?.metapebble_v_ctrl_android?.isNotEmpty() == true) {
            return versionList.metapebble_v_ctrl_android[0]
        }
        return null
    }


}
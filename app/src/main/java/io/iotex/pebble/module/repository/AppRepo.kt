package io.iotex.pebble.module.repository

import com.apollographql.apollo3.ApolloClient
import io.iotex.graphql.metapebble.VersionQuery
import io.iotex.pebble.di.annocation.ApolloClientMetaPebble
import javax.inject.Inject

class AppRepo @Inject constructor(@ApolloClientMetaPebble val mApolloClient: ApolloClient) {

    suspend fun queryVersion(): VersionQuery.Metapebble_v_ctrl_android? {
        val versionList = mApolloClient.query(VersionQuery()).execute().data
        if (versionList?.metapebble_v_ctrl_android?.isNotEmpty() == true) {
            return versionList.metapebble_v_ctrl_android[0]
        }
        return null
    }

}
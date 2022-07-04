package com.machinefi.metapebble.di.module

import com.apollographql.apollo3.ApolloClient
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.machinefi.metapebble.constant.META_PEBBLE_GRAPHQL
import com.machinefi.metapebble.constant.SMART_CONTRACT_GRAPHQL
import com.machinefi.metapebble.constant.TEST_GRAPHQL
import com.machinefi.metapebble.di.annocation.ApolloClientMetaPebble
import com.machinefi.metapebble.di.annocation.ApolloClientSmartContract
import com.machinefi.metapebble.di.annocation.ApolloClientTest
import com.machinefi.metapebble.di.scope.AppScope
import dagger.Module
import dagger.Provides

@Module
class AppModule {

    @AppScope
    @Provides
    @ApolloClientMetaPebble
    fun provideApolloClientMetaPebble(): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl(META_PEBBLE_GRAPHQL)
            .build()
    }

    @AppScope
    @Provides
    @ApolloClientSmartContract
    fun provideApolloClientSmartContract(): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl(SMART_CONTRACT_GRAPHQL)
            .build()
    }

    @AppScope
    @Provides
    @ApolloClientTest
    fun provideApolloClientTest(): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl(TEST_GRAPHQL)
            .build()
    }

    @AppScope
    @Provides
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }
}
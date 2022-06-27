package com.machinefi.metapebble.di.module

import com.apollographql.apollo3.ApolloClient
import com.blankj.utilcode.util.AppUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.machinefi.metapebble.constant.META_PEBBLE_GRAPHQL
import com.machinefi.metapebble.constant.SMART_CONTRACT_GRAPHQL
import com.machinefi.metapebble.constant.TEST_GRAPHQL
import com.machinefi.metapebble.di.annocation.ApolloClientMetaPebble
import com.machinefi.metapebble.di.annocation.ApolloClientSmartContract
import com.machinefi.metapebble.di.annocation.ApolloClientTest
import com.machinefi.metapebble.di.scope.AppScope
import com.machinefi.metapebble.module.http.ApiService
import com.machinefi.metapebble.module.http.HTTP_HOST
import com.machinefi.metapebble.module.http.interceptor.GlobalInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
class AppModule {

    @AppScope
    @Provides
    fun provideRetrofit(client: OkHttpClient, gson: Gson): ApiService {
        return Retrofit.Builder().baseUrl(HTTP_HOST)
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(ApiService::class.java)
    }

    @AppScope
    @Provides
    fun provideOKHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level =
            if (AppUtils.isAppDebug())
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE

        val builder = OkHttpClient.Builder()

        return builder
            .connectTimeout(30, TimeUnit.SECONDS)
            .callTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(GlobalInterceptor())
            .build()
    }

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
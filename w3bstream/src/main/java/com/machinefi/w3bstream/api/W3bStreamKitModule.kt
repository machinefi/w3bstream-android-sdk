package com.machinefi.w3bstream.api

import com.blankj.utilcode.util.AppUtils
import com.google.gson.GsonBuilder
import com.machinefi.w3bstream.common.request.ApiService
import com.machinefi.w3bstream.common.request.interceptor.GlobalInterceptor
import com.machinefi.w3bstream.repository.device.DeviceRepository
import com.machinefi.w3bstream.repository.upload.UploadRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

internal class W3bStreamKitModule(config: W3bStreamKitConfig) {

    init {
        if (config.httpsUploadApi.isBlank()) {
            throw IllegalArgumentException("The parameter httpsUploadApi cannot be an empty")
        }
        if (config.webSocketUploadApi.isBlank()) {
            throw IllegalArgumentException("The parameter webSocketUploadApi cannot be an empty")
        }
    }

    private val okHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level =
            if (AppUtils.isAppDebug())
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE

        val builder = OkHttpClient.Builder()

        builder
            .connectTimeout(10, TimeUnit.SECONDS)
            .callTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(GlobalInterceptor())
            .build()
    }

    private val apiService by lazy {
        val builder = Retrofit.Builder()
            .baseUrl(config.httpsUploadApi)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))

        builder.build().create(ApiService::class.java)
    }

    val uploadManager by lazy {
        UploadRepository(apiService, config)
    }

    val deviceManager by lazy {
        DeviceRepository()
    }

}
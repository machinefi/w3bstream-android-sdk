package com.machinefi.pebblekit.api

import com.blankj.utilcode.util.AppUtils
import com.google.gson.GsonBuilder
import com.machinefi.pebblekit.common.request.ApiService
import com.machinefi.pebblekit.common.request.interceptor.GlobalInterceptor
import com.machinefi.pebblekit.repository.device.DeviceRepository
import com.machinefi.pebblekit.repository.sign.SignRepository
import com.machinefi.pebblekit.repository.upload.HttpUploader
import com.machinefi.pebblekit.repository.upload.UploadRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

internal class PebbleKitModule(config: PebbleKitConfig) {

    private val okHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level =
            if (AppUtils.isAppDebug())
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE

        val builder = OkHttpClient.Builder()

        builder
            .connectTimeout(30, TimeUnit.SECONDS)
            .callTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(GlobalInterceptor())
            .build()
    }

    private val apiService by lazy {
        Retrofit.Builder().baseUrl(config.host)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build().create(ApiService::class.java)
    }

    val uploadManager by lazy {
        UploadRepository(apiService, config)
    }

    val deviceManager by lazy {
        DeviceRepository()
    }

    val signManager by lazy {
        SignRepository(apiService)
    }

}
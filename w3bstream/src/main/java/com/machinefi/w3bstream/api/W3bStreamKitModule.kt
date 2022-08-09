package com.machinefi.w3bstream.api

import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.SPUtils
import com.google.gson.GsonBuilder
import com.machinefi.w3bstream.common.exception.IllegalServerException
import com.machinefi.w3bstream.common.request.ApiService
import com.machinefi.w3bstream.common.request.interceptor.GlobalInterceptor
import com.machinefi.w3bstream.repository.auth.AuthRepository
import com.machinefi.w3bstream.repository.upload.KEY_SERVER_APIS
import com.machinefi.w3bstream.repository.upload.UploadRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL
import java.util.concurrent.TimeUnit

internal class W3bStreamKitModule(config: W3bStreamKitConfig) {

    init {
        if (config.signApi.isBlank()) {
            throw IllegalArgumentException("The parameter authServer cannot be an empty")
        }
        if (config.serverApis.isEmpty()) {
            throw IllegalArgumentException("The parameter serverApis cannot be empty")
        }
        val invalidServer = config.serverApis.firstOrNull {
            !it.startsWith("https://") && !it.startsWith("wss://")
        }
        if (invalidServer != null) {
            throw IllegalServerException("This server $invalidServer is illegal")
        }
        val serverApis = SPUtils.getInstance().getStringSet(KEY_SERVER_APIS)
        serverApis.filter {
            !config.innerServerApis.contains(it)
        }.also {
            config.innerServerApis.addAll(it)
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
        val url = URL(config.signApi)
        val baseUrl = "${url.protocol}:${url.host}"
        val builder = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))

        builder.build().create(ApiService::class.java)
    }

    val authManager by lazy {
        AuthRepository(apiService, config)
    }

    val uploadManager by lazy {
        UploadRepository(apiService, config, authManager)
    }

}
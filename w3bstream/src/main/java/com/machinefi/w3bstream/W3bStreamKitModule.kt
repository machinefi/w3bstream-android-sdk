package com.machinefi.w3bstream

import com.google.gson.GsonBuilder
import com.machinefi.w3bstream.common.exception.IllegalServerException
import com.machinefi.w3bstream.common.request.ApiService
import com.machinefi.w3bstream.constant.NETWORK_TIMEOUT
import com.machinefi.w3bstream.repository.auth.AuthRepository
import com.machinefi.w3bstream.repository.upload.UploadRepository
import com.machinefi.w3bstream.utils.extension.isValidServer
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL
import java.util.concurrent.TimeUnit

internal class W3bStreamKitModule(config: W3bStreamKitConfig) {

    init {
        if (config.signApi.isBlank()) {
            throw IllegalArgumentException("The parameter signApi cannot be an empty")
        }
        if (config.serverApis.isEmpty()) {
            throw IllegalArgumentException("The parameter serverApis cannot be empty")
        }
        val invalidServer = config.serverApis.firstOrNull {
            !it.isValidServer()
        }
        if (invalidServer != null) {
            throw IllegalServerException("This server $invalidServer is illegal")
        }
    }

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .callTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
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
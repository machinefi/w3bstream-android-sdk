package com.machinefi.pebblekit.common.request.interceptor

import okhttp3.Interceptor
import okhttp3.Response

internal class GlobalInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        return chain.proceed(requestBuilder.build())
    }
}
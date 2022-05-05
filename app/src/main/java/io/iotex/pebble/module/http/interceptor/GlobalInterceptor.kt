package io.iotex.pebble.module.http.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class GlobalInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        return chain.proceed(requestBuilder.build())
    }




}
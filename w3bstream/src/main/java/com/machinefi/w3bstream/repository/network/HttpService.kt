package com.machinefi.w3bstream.repository.network

import android.accounts.NetworkErrorException
import com.fasterxml.jackson.core.type.TypeReference
import com.machinefi.w3bstream.BuildConfig
import com.machinefi.w3bstream.utils.JsonUtil
import okhttp3.Headers
import okhttp3.Headers.Companion.toHeaders
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor

class HttpService(private val host: String, private val projectName: String): Service {

    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaTypeOrNull()
    private val headers = HashMap<String, String>()
    private val httpClient = createOkHttpClient()

    override fun <T> send(request: Request<T>, responseType: TypeReference<Response<T>>): Response<T>? {
        val requestBody = request.payload.toRequestBody(JSON_MEDIA_TYPE)
        val headers = buildHeaders()
        val url = "$host/srv-applet-mgr/v0/${request.method}/$projectName"
        val httpRequest = okhttp3.Request.Builder().url(url).headers(headers).post(requestBody).build()
        val response = httpClient.newCall(httpRequest).execute()
        val responseBody = response.body
        val `is` = if (response.isSuccessful) {
            responseBody?.byteStream()
        } else {
            val code = response.code
            val text = responseBody?.string() ?: "N/A"
            throw NetworkErrorException("Invalid response received: $code; $text")
        }

        return `is`?.use {
            JsonUtil.parseJson(it, responseType)
        }?.apply {
            this.id = request.id
            this.url = url
        }
    }

    private fun buildHeaders(): Headers {
        return headers.toHeaders()
    }

    fun addHeader(key: String, value: String) {
        headers[key] = value
    }

    private fun createOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        configureLogging(builder)
        return builder.build()
    }

    private fun configureLogging(builder: OkHttpClient.Builder) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level =
            if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE
        builder.addInterceptor(loggingInterceptor)
    }
}
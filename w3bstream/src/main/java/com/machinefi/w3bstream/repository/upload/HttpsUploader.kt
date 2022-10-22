package com.machinefi.w3bstream.repository.upload

import com.google.gson.Gson
import com.machinefi.w3bstream.W3bStreamKitConfig
import com.machinefi.w3bstream.common.request.ApiService
import com.machinefi.w3bstream.common.request.Header
import com.machinefi.w3bstream.common.request.UploadDataRequest
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

internal const val HTTPS_SCHEMA = "https://"
internal const val HTTP_SCHEMA = "http://"

internal class HttpsUploader(
    private val apiService: ApiService,
    private val config: W3bStreamKitConfig
) : UploadService {

    override fun uploadData(data: String, publisherKey: String, publisherToken: String) {
        if (data.isBlank()) return
        val timestamp = System.currentTimeMillis() / 1000
        val header = Header(0x7FFFFFFF, publisherKey, timestamp, publisherToken)
        val body = UploadDataRequest(header, data)
        val requestBody =
            Gson().toJson(body)
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val urls = config.innerServerApis.filter {
            it.startsWith(HTTPS_SCHEMA) or it.startsWith(HTTP_SCHEMA)
        }
        if (urls.isEmpty()) return
        urls.forEach { url ->
            apiService.uploadData(url, requestBody)
                .subscribeOn(Schedulers.io())
                .subscribe()
        }
    }

}
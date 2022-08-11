package com.machinefi.w3bstream.repository.upload

import com.google.gson.Gson
import com.machinefi.w3bstream.W3bStreamKitConfig
import com.machinefi.w3bstream.common.exception.JsonSyntaxException
import com.machinefi.w3bstream.common.request.ApiService
import com.machinefi.w3bstream.common.request.UploadDataRequest
import com.machinefi.w3bstream.utils.extension.isValidJson
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

internal const val HTTPS_SCHEMA = "https://"

internal class HttpsUploader(
    private val apiService: ApiService,
    private val config: W3bStreamKitConfig
) : UploadService {

    override fun uploadData(json: String, signature: String, pubKey: String) {
        if (json.isBlank()) return
        if (!json.isValidJson()) {
            throw JsonSyntaxException("The json is not a valid representation")
        }
        val data = Gson().fromJson(json, Any::class.java)
        val body = UploadDataRequest(pubKey, signature, data)
        val requestBody =
            Gson().toJson(body)
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val urls = config.serverApis.filter {
            it.startsWith(HTTPS_SCHEMA)
        }
        if (urls.isEmpty()) return
        urls.forEach { url ->
            apiService.uploadData(url, requestBody)
                .subscribeOn(Schedulers.io())
                .subscribe()
        }
    }

}
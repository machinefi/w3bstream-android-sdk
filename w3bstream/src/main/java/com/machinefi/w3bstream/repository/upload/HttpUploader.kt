package com.machinefi.w3bstream.repository.upload

import com.google.gson.Gson
import com.machinefi.w3bstream.api.W3bStreamKitConfig
import com.machinefi.w3bstream.common.exception.JsonSyntaxException
import com.machinefi.w3bstream.common.request.ApiService
import com.machinefi.w3bstream.common.request.UploadDataRequest
import com.machinefi.w3bstream.utils.KeystoreUtil
import com.machinefi.w3bstream.utils.extension.isJsonValid
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

internal const val HTTPS_SCHEMA = "https://"

internal class HttpUploader(
    val apiService: ApiService,
    val config: W3bStreamKitConfig
) : UploadService {

    override fun uploadData(json: String) {
        if (json.isBlank()) return
        if (!json.isJsonValid()) {
            throw JsonSyntaxException("The json is not a valid representation")
        }
        val result = json.toByteArray()
        val signature = KeystoreUtil.signData(result)
        val data = Gson().fromJson(json, Any::class.java)
        val body = UploadDataRequest(KeystoreUtil.getPubKey(), signature, data)
        val requestBody =
            Gson().toJson(body)
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val urls = config.innerServerApis.filter {
            it.startsWith("https://")
        }
        if (urls.isEmpty()) return
        urls.forEach { url ->
            apiService.uploadData(url, requestBody)
                .subscribeOn(Schedulers.io())
                .subscribe()
        }
    }

}
package com.machinefi.pebblekit.repository.upload

import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.machinefi.pebblekit.api.W3bstreamKitConfig
import com.machinefi.pebblekit.common.request.ApiService
import com.machinefi.pebblekit.common.request.UploadDataBody
import com.machinefi.pebblekit.constant.SP_KEY_HTTPS_SERVER
import com.machinefi.pebblekit.constant.SP_KEY_IMEI
import com.machinefi.pebblekit.constant.SP_NAME
import com.machinefi.pebblekit.uitls.KeystoreUtil
import com.machinefi.pebblekit.uitls.extension.isJsonValid
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

internal class HttpUploader(
    val apiService: ApiService,
    val config: W3bstreamKitConfig
) : UploadService {

    override fun uploadData(json: String) {
        if (json.isBlank()) return
        if (!json.isJsonValid()) {
            throw JsonSyntaxException("The json is not a valid representation")
        }
        val result = json.toByteArray()
        val signature = KeystoreUtil.signData(result)
        val data = Gson().fromJson(json, Any::class.java)
        val imei = SPUtils.getInstance(SP_NAME).getString(SP_KEY_IMEI)
        if (imei.isNullOrBlank()) return
        val body = UploadDataBody(imei, KeystoreUtil.getPubKey() ?: "", signature, data)
        val requestBody =
            Gson().toJson(body)
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val url = SPUtils.getInstance().getString(SP_KEY_HTTPS_SERVER, config.httpsUploadApi)
        if (url.isNullOrBlank()) return
        apiService.uploadMetadata(url, requestBody)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

}
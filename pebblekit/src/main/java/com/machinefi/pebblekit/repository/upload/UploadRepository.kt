package com.machinefi.pebblekit.repository.upload

import android.annotation.SuppressLint
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.TimeUtils
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.machinefi.pebblekit.api.PebbleKitConfig
import com.machinefi.pebblekit.common.request.ApiService
import com.machinefi.pebblekit.common.request.UploadDataBody
import com.machinefi.pebblekit.constant.SP_KEY_IMEI
import com.machinefi.pebblekit.constant.SP_KEY_UPLOAD_FREQUENCY
import com.machinefi.pebblekit.constant.SP_NAME
import com.machinefi.pebblekit.uitls.EncryptUtil
import com.machinefi.pebblekit.uitls.KeystoreUtil
import com.machinefi.pebblekit.uitls.extension.isJsonValid
import com.machinefi.pebblekit.uitls.extension.toHexByteArray
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

internal class UploadRepository(
    val apiService: ApiService,
    val config: PebbleKitConfig
) : UploadManager {

    private var pollingComposite = CompositeDisposable()

    @SuppressLint("CheckResult")
    private fun polling(callback: () -> Unit) {
        val interval = SPUtils.getInstance(SP_NAME).getInt(SP_KEY_UPLOAD_FREQUENCY, INTERVAL_SEND_DATA)
        Observable.interval(0, interval.toLong(), TimeUnit.SECONDS)
            .doOnSubscribe {
                pollingComposite.add(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                callback.invoke()
            }
    }

    override fun startUploading(w3bStreamServer: String, hook: () -> String) {
        stopUploading()
        pollingComposite = CompositeDisposable()
        polling {
            val json = hook.invoke()
            if (json.isBlank()) return@polling
            if (!json.isJsonValid()) {
                throw JsonSyntaxException("The json is not a valid representation")
            }
            val result = json.toByteArray()
            val signature = KeystoreUtil.signData(result)
            val data = Gson().fromJson(json, Any::class.java)
            val imei = SPUtils.getInstance(SP_NAME).getString(SP_KEY_IMEI)
            if (imei.isNullOrBlank()) return@polling
            val body = UploadDataBody(imei, KeystoreUtil.getPubKey() ?: "", signature, data)
            val requestBody =
                Gson().toJson(body)
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            apiService.uploadMetadata(config.w3bStreamServer, requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        }
    }

    override fun stopUploading() {
        pollingComposite.dispose()
        pollingComposite.clear()
    }

    override fun uploadFrequency(frequency: Int) {
        SPUtils.getInstance(SP_NAME).put(SP_KEY_UPLOAD_FREQUENCY, frequency)
    }

}
package com.machinefi.pebblekit.repository.upload

import android.annotation.SuppressLint
import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.machinefi.pebblekit.api.PebbleKitConfig
import com.machinefi.pebblekit.common.request.ApiService
import com.machinefi.pebblekit.constant.SP_KEY_GPS_CHECKED
import com.machinefi.pebblekit.constant.SP_KEY_SUBMIT_FREQUENCY
import com.machinefi.pebblekit.uitls.extension.isJsonValid
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

const val INTERVAL_SEND_DATA = 5

internal class UploadRepository(
    val apiService: ApiService,
    val config: PebbleKitConfig
) : UploadManager {

    private var pollingComposite = CompositeDisposable()

    @SuppressLint("CheckResult")
    private fun polling(callback: () -> Unit) {
        if (!SPUtils.getInstance().getBoolean(SP_KEY_GPS_CHECKED, true)) return
        val interval = SPUtils.getInstance().getInt(SP_KEY_SUBMIT_FREQUENCY, INTERVAL_SEND_DATA)
        Observable.interval(0, interval.toLong(), TimeUnit.MINUTES)
            .doOnSubscribe {
                pollingComposite.add(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                callback.invoke()
            }
    }

    override suspend fun startUploading(hook: () -> String) {
        stopUploading()
        pollingComposite = CompositeDisposable()
        polling {
            val json = hook.invoke()
            if (!json.isJsonValid()) {
                throw JsonSyntaxException("The json is not a valid representation")
            }
            val requestBody =
                Gson().toJson(json).toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            apiService.uploadMetadata(config.w3bStreamServer, requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        }
    }

    override suspend fun stopUploading() {
        pollingComposite.dispose()
        pollingComposite.clear()
    }

}
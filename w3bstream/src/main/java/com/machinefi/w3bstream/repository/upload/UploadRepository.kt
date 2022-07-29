package com.machinefi.w3bstream.repository.upload

import android.annotation.SuppressLint
import com.blankj.utilcode.util.SPUtils
import com.machinefi.w3bstream.api.W3bStreamKitConfig
import com.machinefi.w3bstream.common.request.ApiService
import com.machinefi.w3bstream.constant.SP_KEY_HTTPS_SERVER
import com.machinefi.w3bstream.constant.SP_KEY_SOCKET_SERVER
import com.machinefi.w3bstream.constant.SP_KEY_UPLOAD_FREQUENCY
import com.machinefi.w3bstream.constant.SP_NAME
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

internal class UploadRepository(
    apiService: ApiService,
    val config: W3bStreamKitConfig
): UploadManager {

    private var pollingComposite = CompositeDisposable()

    private val httpUploader = HttpUploader(apiService, config)
    private val webSocketUploader = WebSocketUploader(config).apply {
        this.connect()
    }

    @SuppressLint("CheckResult")
    private fun polling(callback: () -> Unit) {
        val interval = SPUtils.getInstance(SP_NAME).getLong(SP_KEY_UPLOAD_FREQUENCY, INTERVAL_SEND_DATA)
        Observable.interval(0, interval, TimeUnit.SECONDS)
            .doOnSubscribe {
                pollingComposite.add(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                callback.invoke()
            }
    }

    override fun startUpload(hook: () -> String) {
        stopUpload()
        pollingComposite = CompositeDisposable()
        polling {
            val json = hook.invoke()
            httpUploader.uploadData(json)
            webSocketUploader.uploadData(json)
        }
    }

    override fun stopUpload() {
        pollingComposite.dispose()
        pollingComposite.clear()
        webSocketUploader.close()
    }

    override fun setUploadInterval(seconds: Long) {
        SPUtils.getInstance(SP_NAME).put(SP_KEY_UPLOAD_FREQUENCY, seconds)
    }

    override fun setHttpsServerApi(api: String) {
        SPUtils.getInstance(SP_NAME).put(SP_KEY_HTTPS_SERVER, api)
    }

    override fun setWebSocketServerApi(api: String) {
        SPUtils.getInstance(SP_NAME).put(SP_KEY_SOCKET_SERVER, api)
        webSocketUploader.resume(api)
    }
}
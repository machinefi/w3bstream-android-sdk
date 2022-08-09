package com.machinefi.w3bstream.repository.upload

import android.annotation.SuppressLint
import com.blankj.utilcode.util.SPUtils
import com.machinefi.w3bstream.api.W3bStreamKitConfig
import com.machinefi.w3bstream.common.request.ApiService
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

    override fun addServerApi(api: String) {
        if (!config.innerServerApis.contains(api)) {
            config.innerServerApis.add(api)
            val serverApis = SPUtils.getInstance().getStringSet(KEY_SERVER_APIS).toMutableSet()
            serverApis.add(api)
            SPUtils.getInstance().put(KEY_SERVER_APIS, serverApis)
        }
        if (api.startsWith(WEB_SOCKET_SCHEMA)) {
            webSocketUploader.resume()
        }
    }

    override fun addServerApis(apis: List<String>) {
        apis.filter {
            !config.innerServerApis.contains(it)
        }.also {
            config.innerServerApis.addAll(it)
            val serverApis = SPUtils.getInstance().getStringSet(KEY_SERVER_APIS).toMutableSet()
            serverApis.addAll(apis)
            SPUtils.getInstance().put(KEY_SERVER_APIS, serverApis)
        }
        val webSocketApi = apis.firstOrNull { it.startsWith(WEB_SOCKET_SCHEMA) }
        if (webSocketApi != null) {
            webSocketUploader.resume()
        }
    }

    override fun removeServerApi(api: String) {
        if (config.innerServerApis.contains(api)) {
            config.innerServerApis.remove(api)
        }
        if (api.startsWith(WEB_SOCKET_SCHEMA)) {
            webSocketUploader.resume()
        }
    }

    private fun isValidApi(api: String): Boolean {
        return api.startsWith(HTTPS_SCHEMA) || api.startsWith(WEB_SOCKET_SCHEMA)
    }
}
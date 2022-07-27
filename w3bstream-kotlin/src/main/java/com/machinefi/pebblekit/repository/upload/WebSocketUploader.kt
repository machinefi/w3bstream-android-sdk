package com.machinefi.pebblekit.repository.upload

import android.annotation.SuppressLint
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.TimeUtils
import com.google.gson.Gson
import com.machinefi.pebblekit.api.W3bstreamKitConfig
import com.machinefi.pebblekit.common.request.JSONRpcBody
import com.machinefi.pebblekit.common.request.JSONRpcParams
import com.machinefi.pebblekit.common.request.UploadDataBody
import com.machinefi.pebblekit.constant.SP_KEY_IMEI
import com.machinefi.pebblekit.constant.SP_KEY_SOCKET_SERVER
import com.machinefi.pebblekit.constant.SP_NAME
import com.machinefi.pebblekit.uitls.KeystoreUtil
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI
import java.util.concurrent.TimeUnit

class WebSocketUploader(
    val config: W3bstreamKitConfig
): UploadService {

    private var client: WebSocketClient? = null
    private var pulseDispose: Disposable? = null

    init {
        initSocketClient()
    }

    private fun initSocketClient() {
        val url = SPUtils.getInstance().getString(SP_KEY_SOCKET_SERVER, config.webSocketUploadApi)
        if (url.isNullOrBlank()) return
        client = createClient(url)
    }

    private fun createClient(url: String): WebSocketClient {
        val uri = URI(url)
        return object : WebSocketClient(uri) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                LogUtils.i("onOpen")
                pulse()
            }

            override fun onMessage(message: String?) {
                LogUtils.i("onMessage", message)
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
            }

            override fun onError(ex: Exception?) {
                LogUtils.i("onError", ex?.message)
            }
        }
    }

    fun resume(url: String) {
        close()
        client = createClient(url)
    }

    fun connect() {
        if (client?.isOpen != true) {
            client?.connect()
        }
    }

    fun close() {
        pulseDispose?.dispose()
        client?.close()
        client = null
    }

    @SuppressLint("CheckResult")
    private fun pulse() {
        Observable.interval(0, 10, TimeUnit.SECONDS)
            .doOnSubscribe {
                pulseDispose = it
            }
            .observeOn(Schedulers.io())
            .subscribe {
                if (client == null) {
                    initSocketClient()
                } else {
                    if (client?.isClosed == true) {
                        client?.reconnect()
                    }
                }
            }
    }

    override fun uploadData(json: String) {
        if (client?.isOpen != true) return
        val data = Gson().fromJson(json, Any::class.java)
        val id = TimeUtils.getNowMills()
        val imei = SPUtils.getInstance(SP_NAME).getString(SP_KEY_IMEI)
        val signature = KeystoreUtil.signData(json.toByteArray())
        val dataBody = UploadDataBody(imei, KeystoreUtil.getPubKey() ?: "", signature, data)
        val jsonRpcParams = JSONRpcParams(dataBody)
        val jsonRpcBody = JSONRpcBody(id, params = jsonRpcParams)
        client?.send(Gson().toJson(jsonRpcBody).toByteArray())
    }

}
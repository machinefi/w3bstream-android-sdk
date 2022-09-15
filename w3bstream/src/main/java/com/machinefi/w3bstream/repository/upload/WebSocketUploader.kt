package com.machinefi.w3bstream.repository.upload

import android.annotation.SuppressLint
import com.blankj.utilcode.util.TimeUtils
import com.google.gson.Gson
import com.machinefi.w3bstream.W3bStreamKitConfig
import com.machinefi.w3bstream.common.request.JSONRpcParams
import com.machinefi.w3bstream.common.request.JSONRpcRequest
import com.machinefi.w3bstream.common.request.UploadDataRequest
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.util.concurrent.TimeUnit

internal const val WEB_SOCKET_SCHEMA = "wss://"

internal class WebSocketUploader(
    private val config: W3bStreamKitConfig
): UploadService {

    private val clients = mutableListOf<WebSocketClient>()
    private var pulseDispose: Disposable? = null

    init {
        initSocketClient()
    }

    private fun initSocketClient() {
        config.serverApis.filter {
            it.startsWith(WEB_SOCKET_SCHEMA)
        }.forEach { url ->
            if (url.isNotBlank()) {
                val client = createClient(url).apply {
                    if (!this.isOpen) {
                        this.connect()
                    }
                }
                clients.add(client)
            }
        }
    }

    private fun createClient(url: String): WebSocketClient {
        val uri = URI(url)
        return object : WebSocketClient(uri) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                pulse(uri.toString())
            }

            override fun onMessage(message: String?) {
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
            }

            override fun onError(ex: Exception?) {
                ex?.printStackTrace()
            }
        }
    }

    fun resume() {
        close()
        initSocketClient()
    }

    private fun close() {
        pulseDispose?.dispose()
        clients.forEach { client ->
            client.close()
        }
        clients.clear()
    }

    @SuppressLint("CheckResult")
    private fun pulse(uri: String) {
        Observable.interval(0, 10, TimeUnit.SECONDS)
            .doOnSubscribe {
                pulseDispose = it
            }
            .observeOn(Schedulers.io())
            .subscribe {
                val client = clients.firstOrNull { client ->
                    client.uri?.toString() == uri
                }
                if (client?.isClosed == true) {
                    client.reconnect()
                }
            }
    }

    override fun uploadData(json: String, signature: String, pubKey: String) {
        if (clients.isEmpty()) return
        val data = Gson().fromJson(json, Any::class.java)
        val id = TimeUtils.getNowMills()
        val dataBody = UploadDataRequest(pubKey, signature, data)
        val jsonRpcParams = JSONRpcParams(dataBody)
        val jsonRpcBody = JSONRpcRequest(id, params = jsonRpcParams)
        val result = Gson().toJson(jsonRpcBody).toByteArray()
        clients.forEach { client ->
            if (client.isOpen) {
                client.send(result)
            }
        }
    }

}
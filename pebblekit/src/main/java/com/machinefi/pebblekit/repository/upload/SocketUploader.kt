package com.machinefi.pebblekit.repository.upload

import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.TimeUtils
import com.google.gson.Gson
import com.machinefi.pebblekit.api.PebbleKitConfig
import com.machinefi.pebblekit.common.request.JSONRpcBody
import com.machinefi.pebblekit.common.request.JSONRpcParams
import com.machinefi.pebblekit.constant.SP_KEY_SOCKET_SERVER
import com.xuhao.didi.core.iocore.interfaces.IPulseSendable
import com.xuhao.didi.core.iocore.interfaces.ISendable
import com.xuhao.didi.socket.client.sdk.OkSocket
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo
import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager
import kotlinx.coroutines.*
import java.net.InetAddress
import java.net.SocketAddress

internal class SocketUploader(
    config: PebbleKitConfig
): UploadService {

    private var mSocketManager: IConnectionManager? = null

    init {
        GlobalScope.launch {
            val url = SPUtils.getInstance().getString(SP_KEY_SOCKET_SERVER, config.socketUploadUrl)
            mSocketManager = createSocket(url)
        }
    }

    private suspend fun createSocket(url: String): IConnectionManager? {
        return withContext(Dispatchers.IO) {
            kotlin.runCatching {
                LogUtils.i("url", url)
//                val ip = InetAddress.getByName(url).hostAddress
//                LogUtils.i("ip", ip)
                val info = ConnectionInfo(url, 443)
                OkSocket.open(info).apply {
                    this.registerReceiver(object : SocketActionAdapter() {
                        override fun onSocketConnectionSuccess(info: ConnectionInfo, action: String) {
                            LogUtils.i("onSocketConnectionSuccess")
                            LogUtils.i("action", action)
                            OkSocket.open(info)
                                .pulseManager
                                .setPulseSendable(PulseData())
                                .pulse()
                        }

                        override fun onSocketConnectionFailed(info: ConnectionInfo?, action: String?, e: Exception?) {
                            super.onSocketConnectionFailed(info, action, e)
                            LogUtils.i("onSocketConnectionFailed")
                            LogUtils.i("action", action)
                        }
                    })
                }
            }.getOrNull()
        }
    }

    fun resume(url: String) {
        GlobalScope.launch {
            mSocketManager = createSocket(url)
            connect()
        }
    }

    fun connect() {
        if (mSocketManager?.isConnect == false) {
            mSocketManager?.connect()
        }
    }

    fun disconnect() {
        mSocketManager?.disconnect()
    }

    override fun uploadData(json: String) {
        mSocketManager?.send(SensorSendData(json))
    }

}

internal class SensorSendData(val json: String): ISendable {

    override fun parse(): ByteArray {
        val data = Gson().fromJson(json, Any::class.java)
        val id = TimeUtils.getNowMills()
        val params = JSONRpcParams(data)
        val body = JSONRpcBody(id, params = params)
        return Gson().toJson(body).toByteArray()
    }

}

internal class PulseData : IPulseSendable {
    override fun parse(): ByteArray {
        val id = TimeUtils.getNowMills()
        val params = JSONRpcParams("{}")
        val body = JSONRpcBody(id, params = params)
        return Gson().toJson(body).toByteArray()
    }
}
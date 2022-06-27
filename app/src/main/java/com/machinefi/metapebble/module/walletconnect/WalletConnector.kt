package com.machinefi.metapebble.module.walletconnect

import com.blankj.utilcode.util.Utils
import com.machinefi.metapebble.module.walletconnect.api.WalletConnectKit
import com.machinefi.metapebble.module.walletconnect.api.WalletConnectKitConfig
import com.machinefi.metapebble.utils.extension.i
import com.machinefi.metapebble.utils.extension.safeLet
import org.walletconnect.Session

object WalletConnector : Session.Callback {

    private val config by lazy {
        WalletConnectKitConfig(
            context = Utils.getApp(),
            bridgeUrl = "https://bridge.walletconnect.org",
            appUrl = "",
            appName = "MetaPebble",
            appDescription = ""
        )
    }
    private val walletConnectKit by lazy { WalletConnectKit.Builder(config).build() }

    private lateinit var onConnected: (address: String, chainId: Long) -> Unit
    private var onDisconnected: (() -> Unit)? = null

    val walletAddress get() = walletConnectKit.address
    val chainId get() = walletConnectKit.chainId

    private var isConnected = false

    fun init(
        onConnected: (address: String, chainId: Long) -> Unit,
        onDisconnected: (() -> Unit)?
    ) {
        this.onConnected = onConnected
        this.onDisconnected = onDisconnected
        loadSessionIfStored()
    }

    fun connect() {
        if (walletConnectKit.isSessionStored) {
            walletConnectKit.removeSession()
        }
        walletConnectKit.createSession(this)
    }

    fun isConnected(): Boolean {
        return walletConnectKit.isSessionStored && isConnected
    }

    fun disconnect() {
        walletConnectKit.removeSession()
    }

    override fun onStatus(status: Session.Status) {
        "WalletConnector : ${status}".i()
        when (status) {
            is Session.Status.Approved -> onSessionApproved()
            is Session.Status.Connected -> onSessionConnected()
            is Session.Status.Closed -> onSessionDisconnected()
            else -> {}
        }
    }

    override fun onMethodCall(call: Session.MethodCall) {
    }

    private fun onSessionApproved() {
        isConnected = true
        safeLet(walletConnectKit.address, walletConnectKit.chainId, onConnected)
    }

    private fun onSessionConnected() {
        walletConnectKit.address ?: walletConnectKit.requestHandshake()
    }

    private fun onSessionDisconnected() {
        isConnected = false
        if (walletConnectKit.isSessionStored) {
            walletConnectKit.removeSession()
        }
        onDisconnected?.invoke()
    }

    private fun loadSessionIfStored() {
        if (walletConnectKit.isSessionStored) {
            walletConnectKit.loadSession(this)
            safeLet(walletConnectKit.address, walletConnectKit.chainId, onConnected)
        }
    }

    suspend fun sendTransaction(
        address: String,
        value: String,
        data: String?,
        nonce: String? = null,
        gasPrice: String? = null,
        gasLimit: String? = null
    ): Session.MethodCall.Response {
        return walletConnectKit.performTransaction(address, value, data, nonce, gasPrice, gasLimit)
    }

    suspend fun signMessage(address: String, message: String): Session.MethodCall.Response {
        return walletConnectKit.performSignMessage(address, message)
    }

    suspend fun signTransaction(params: List<*>): Session.MethodCall.Response {
        return walletConnectKit.performCustomMethod("eth_signTransaction", params)
    }

    suspend fun signTypeData(params: List<*>): Session.MethodCall.Response {
        return walletConnectKit.performCustomMethod("eth_signTypedData", params)
    }

    suspend fun personalSign(params: List<*>): Session.MethodCall.Response {
        return walletConnectKit.performCustomMethod("personal_sign", params)
    }

}
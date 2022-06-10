package io.iotex.pebble.module.walletconnect.repository.wallet

import org.walletconnect.Session

interface WalletManager {
    fun openWallet()
    fun requestHandshake()

    suspend fun performTransaction(
        address: String,
        value: String,
        data: String?,
        nonce: String? = null,
        gasPrice: String? = null,
        gasLimit: String? = null,
    ): Session.MethodCall.Response

    suspend fun performSignMessage(address: String, message: String): Session.MethodCall.Response

    suspend fun performCustomMethod(method: String, params: List<*>): Session.MethodCall.Response

}
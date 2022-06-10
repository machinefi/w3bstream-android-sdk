package io.iotex.pebble.module.walletconnect.repository.wallet

import android.content.Intent
import android.net.Uri
import io.iotex.pebble.module.walletconnect.api.WalletConnectKitConfig
import io.iotex.pebble.module.walletconnect.repository.session.SessionRepository
import io.iotex.pebble.utils.extension.toHexString
import io.iotex.pebble.utils.extension.toWei
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.walletconnect.Session
import kotlin.coroutines.Continuation
import kotlin.coroutines.suspendCoroutine

internal class WalletRepository(
    private val walletConnectKitConfig: WalletConnectKitConfig,
    private val sessionRepository: SessionRepository,
) : WalletManager {

    override fun openWallet() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("wc:")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        walletConnectKitConfig.context.startActivity(intent)
    }

    override fun requestHandshake() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(sessionRepository.wcUri)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        walletConnectKitConfig.context.startActivity(intent)
    }

    override suspend fun performTransaction(
        address: String,
        value: String,
        data: String?,
        nonce: String?,
        gasPrice: String?,
        gasLimit: String?,
    ): Session.MethodCall.Response {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                sessionRepository.address?.let { fromAddress ->
                    sessionRepository.session?.let { session ->
                        val id = System.currentTimeMillis()
                        session.performMethodCall(
                            Session.MethodCall.SendTransaction(
                                id,
                                fromAddress,
                                address,
                                nonce,
                                gasPrice,
                                gasLimit,
                                value.toWei().toHexString(),
                                data ?: ""
                            )
                        ) { response -> handleResponse(id, response, continuation) }
                        openWallet()
                    } ?: continuation.resumeWith(Result.failure(Throwable("Session not found!")))
                } ?: continuation.resumeWith(Result.failure(Throwable("Address not found!")))
            }
        }
    }

    override suspend fun performSignMessage(
        address: String,
        message: String
    ): Session.MethodCall.Response {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                sessionRepository.session?.let { session ->
                    val id = System.currentTimeMillis()
                    session.performMethodCall(
                        Session.MethodCall.SignMessage(
                            id,
                            address,
                            message
                        )
                    ) { response -> handleResponse(id, response, continuation) }
                    openWallet()
                } ?: continuation.resumeWith(Result.failure(Throwable("Session not found!")))
            }
        }
    }

    override suspend fun performCustomMethod(
        method: String,
        params: List<*>
    ): Session.MethodCall.Response {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                val id = System.currentTimeMillis()
                sessionRepository.session?.performMethodCall(
                    Session.MethodCall.Custom(id, method, params)
                ) { response ->
                    handleResponse(id, response, continuation)
                } ?: continuation.resumeWith(Result.failure(Throwable("Session not found!")))
                openWallet()
            }
        }
    }

    private fun handleResponse(
        id: Long,
        response: Session.MethodCall.Response,
        continuation: Continuation<Session.MethodCall.Response>
    ) {
        if (id != response.id) {
            val throwable = Throwable("The response id is different from the transaction id!")
            continuation.resumeWith(Result.failure(throwable))
            return
        }
        response.error?.let {
            continuation.resumeWith(Result.failure(Throwable(it.message)))
        } ?: continuation.resumeWith(Result.success(response))
    }
}
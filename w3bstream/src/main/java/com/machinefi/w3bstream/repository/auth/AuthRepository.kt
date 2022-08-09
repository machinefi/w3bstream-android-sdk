package com.machinefi.w3bstream.repository.auth

import com.machinefi.w3bstream.api.W3bStreamKitConfig
import com.machinefi.w3bstream.common.exception.AuthException
import com.machinefi.w3bstream.common.request.ApiService
import com.machinefi.w3bstream.common.request.AuthRequest
import com.machinefi.w3bstream.common.request.AuthResult
import com.machinefi.w3bstream.common.request.BaseResp
import com.machinefi.w3bstream.utils.KeystoreUtil
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlin.coroutines.suspendCoroutine

internal class AuthRepository(
    val apiService: ApiService,
    val config: W3bStreamKitConfig
): AuthManager {

    override suspend fun authenticate(
        imei: String,
        sn: String,
        pubKey: String,
        signature: String
    ): AuthResult {
        return suspendCoroutine { continuation ->
            val body = AuthRequest(imei, sn, pubKey, signature)
            apiService.sign(config.signApi, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<BaseResp<AuthResult>> {
                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(t: BaseResp<AuthResult>) {
                        if (t.result != null) {
                            continuation.resumeWith(Result.success(t.result.data))
                        } else {
                            continuation.resumeWith(Result.failure(AuthException(t.error?.message ?: "")))
                        }
                    }

                    override fun onError(e: Throwable) {
                        continuation.resumeWith(Result.failure(AuthException(e.message ?: "")))
                    }

                    override fun onComplete() {
                    }

                })
        }
    }

    override suspend fun signData(data: ByteArray): String {
        return KeystoreUtil.signData(data)
    }

    override suspend fun getPublicKey(): String {
        return KeystoreUtil.getPubKey()
    }
}
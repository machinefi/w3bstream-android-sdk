package com.machinefi.pebblekit.repository.sign

import com.machinefi.pebblekit.common.request.ApiService
import com.machinefi.pebblekit.common.request.ErrorHandleSubscriber
import com.machinefi.pebblekit.common.request.SignPebbleBody
import com.machinefi.pebblekit.common.request.SignPebbleResult
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlin.coroutines.suspendCoroutine

internal class SignRepository(
    val apiService: ApiService
): SignManager {

    override suspend fun sign(imei: String, sn: String, pubKey: String): SignPebbleResult {
        return suspendCoroutine { continuation ->
            val body = SignPebbleBody(imei, sn, pubKey)
            apiService.signPebble(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ErrorHandleSubscriber<SignPebbleResult>() {
                    override fun onSuccess(t: SignPebbleResult) {
                        continuation.resumeWith(Result.success(t))
                    }

                    override fun onFail(code: Int, msg: String) {
                        continuation.resumeWith(Result.failure(SignException(msg)))
                    }
                })
        }

    }

}
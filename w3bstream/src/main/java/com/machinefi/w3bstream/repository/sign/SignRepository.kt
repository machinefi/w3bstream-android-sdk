package com.machinefi.w3bstream.repository.sign

import com.machinefi.w3bstream.common.exception.SignException
import com.machinefi.w3bstream.common.request.ApiService
import com.machinefi.w3bstream.common.request.BaseResp
import com.machinefi.w3bstream.common.request.SignDeviceBody
import com.machinefi.w3bstream.common.request.SignDeviceResult
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlin.coroutines.suspendCoroutine

internal class SignRepository(
    val apiService: ApiService
): SignManager {

    override suspend fun sign(imei: String, sn: String, pubKey: String): SignDeviceResult {
        return suspendCoroutine { continuation ->
            val body = SignDeviceBody(imei, sn, pubKey)
            apiService.signPebble(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<BaseResp<SignDeviceResult>> {
                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(t: BaseResp<SignDeviceResult>) {
                        if (t.success) {
                            continuation.resumeWith(Result.success(t.data!!))
                        } else {
                            continuation.resumeWith(Result.failure(SignException(t.error?.message ?: "")))
                        }
                    }

                    override fun onError(e: Throwable) {
                        continuation.resumeWith(Result.failure(SignException(e.message ?: "")))
                    }

                    override fun onComplete() {
                    }

                })
        }

    }

}
package com.machinefi.metapebble.module.http

import com.blankj.utilcode.util.Utils
import com.google.gson.JsonIOException
import com.google.gson.JsonParseException
import com.machinefi.metapebble.R
import com.machinefi.metapebble.utils.extension.toast
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.ParseException

abstract class ErrorHandleSubscriber<T> : Observer<BaseResp<T>> {

    override fun onComplete() {
    }

    override fun onSubscribe(d: Disposable) {
    }

    override fun onError(e: Throwable) {
        e.printStackTrace()
        val msg = if (e is UnknownHostException || e is ConnectException) {
            Utils.getApp()?.getString(R.string.network_unavailable)
        } else if (e is SocketTimeoutException) {
            Utils.getApp()?.getString(R.string.network_timeout)
        } else if (e is HttpException) {
            convertStatusCode(e)
        } else if (e is JsonParseException || e is ParseException || e is JSONException || e is JsonIOException) {
            Utils.getApp()?.getString(R.string.data_parsing_error)
        } else {
            ""
        }

        this.onComplete()

        if (msg.isNullOrBlank())
            return

        msg.toast()
    }

    private fun convertStatusCode(httpException: HttpException): String {
        return when {
            httpException.code() == SERVER_ERROR -> Utils.getApp()
                ?.getString(R.string.server_error)!!
            httpException.code() == NOT_FOUND -> Utils.getApp()
                ?.getString(R.string.request_not_exist)!!
            httpException.code() == REQUEST_REFUSED -> Utils.getApp()
                ?.getString(R.string.request_not_exist)!!
            httpException.code() == REQUEST_REDIRECTED -> Utils.getApp()
                ?.getString(R.string.request_redirected)!!
            else -> httpException.message()
        }
    }

    override fun onNext(t: BaseResp<T>) {
        if (t.success) {
            if (t.data != null) {
                onSuccess(t.data)
            }
        } else {
            if (t.error != null) {
                onFail(t.error.code)
                t.error.message.toast()
            }
        }
    }

    abstract fun onSuccess(t: T)

    open fun onFail(code: Int) {}


}
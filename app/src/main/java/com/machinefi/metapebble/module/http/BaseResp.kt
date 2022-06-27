package com.machinefi.metapebble.module.http

const val NOT_FOUND = 404
const val SUCCESS = 0
const val ERROR_EXPIRED = 401
const val REQUEST_REFUSED = 403
const val REQUEST_REDIRECTED = 307
const val SERVER_ERROR = 500

class BaseResp<T>(val success: Boolean, val data: T?, val error: Error?) {

    companion object {

        fun <T> success(data: T?): BaseResp<T> {
            return BaseResp(true, data, null)
        }

        fun <T> error(error: Error?): BaseResp<T> {
            return BaseResp(false, null, error)
        }

//        fun <T> pub_loading(data: T?): BaseResp<T> {
//            return BaseResp(LOADING, data, null)
//        }
    }
}

data class Error(
    val code: Int,
    val message: String
)

package com.machinefi.pebblekit.common.request

const val NOT_FOUND = 404
const val SUCCESS = 0
const val ERROR_EXPIRED = 401
const val REQUEST_REFUSED = 403
const val REQUEST_REDIRECTED = 307
const val SERVER_ERROR = 500

internal class BaseResp<T>(val success: Boolean, val data: T?, val error: Error?)

data class Error(
    val code: Int,
    val message: String
)
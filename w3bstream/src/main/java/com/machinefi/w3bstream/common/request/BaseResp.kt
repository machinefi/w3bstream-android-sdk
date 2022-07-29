package com.machinefi.w3bstream.common.request

internal class BaseResp<T>(val success: Boolean, val data: T?, val error: Error?)

data class Error(
    val code: Int,
    val message: String
)
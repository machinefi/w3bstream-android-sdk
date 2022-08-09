package com.machinefi.w3bstream.common.request

internal class BaseResp<T>(val result: Result<T>?, val error: Error?)

data class Error(
    val code: Int,
    val message: String
)

data class Result<T>(
    val type: String,
    val data: T
)
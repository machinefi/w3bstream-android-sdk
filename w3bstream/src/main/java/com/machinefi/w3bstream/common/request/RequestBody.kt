package com.machinefi.w3bstream.common.request

import java.io.Serializable

internal data class AuthRequest(
    val imei: String,
    val sn: String,
    val pubKey: String,
    val signature: String
)

internal data class UploadDataRequest(
    val pubKey: String,
    val signature: String,
    val data: Any
) : Serializable

internal data class JSONRpcRequest(
    val id: Long,
    val jsonrpc: String = "2.0",
    val method: String = "mutation",
    val params: JSONRpcParams
)

internal data class JSONRpcParams(
    val input: Any,
    val path: String = "data",
)
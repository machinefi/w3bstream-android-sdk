package com.machinefi.pebblekit.common.request

internal data class SignPebbleResp(
    val imei: String,
    val sn: String,
    val timestamp: Long,
    val hash: String,
    val authentication: String
)

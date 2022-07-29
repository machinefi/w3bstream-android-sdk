package com.machinefi.w3bstream.common.request

data class SignDeviceResult(
    val imei: String,
    val sn: String,
    val timestamp: Long,
    val hash: String,
    val authentication: String
)

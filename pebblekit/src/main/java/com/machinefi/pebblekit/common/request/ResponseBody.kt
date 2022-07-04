package com.machinefi.pebblekit.common.request

data class SignPebbleResult(
    val imei: String,
    val sn: String,
    val timestamp: Long,
    val hash: String,
    val authentication: String
)

package com.machinefi.pebblekit.common.request

import java.io.Serializable

internal data class SignPebbleBody(
    val imei: String,
    val sn: String,
    val pubkey: String
)

internal data class UploadDataBody(
    val imei: String,
    val pubKey: String,
    val signature: String,
    val data: Any
) : Serializable

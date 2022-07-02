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
    val data: SensorData
) : Serializable

internal data class SensorData(
    val snr: Int,
    val latitude: String,
    val longitude: String,
    val random: String,
    val timestamp: Long
) : Serializable


package com.machinefi.metapebble.module.http

import java.io.Serializable

data class SignPebbleBody(
    val imei: String,
    val sn: String,
    val pubkey: String
)

data class UploadDataBody(
    val imei: String,
    val pubKey: String,
    val type: Int,
    val signature: String,
    val timestamp: String,
    val data: SensorData
): Serializable

data class SensorData(
    val snr: Int,
    val latitude: String,
    val longitude: String,
    val random: String
): Serializable


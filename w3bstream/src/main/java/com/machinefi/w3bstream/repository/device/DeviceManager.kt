package com.machinefi.w3bstream.repository.device

internal const val LEN_IMEI = 15
internal const val LEN_SN = 10

internal interface DeviceManager {

    suspend fun createDevice(): Device

}

data class Device(
    val imei: String,
    val sn: String,
    val pubKey: String
)
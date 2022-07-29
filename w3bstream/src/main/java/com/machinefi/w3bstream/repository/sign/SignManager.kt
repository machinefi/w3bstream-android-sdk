package com.machinefi.w3bstream.repository.sign

import com.machinefi.w3bstream.common.request.SignDeviceResult

internal interface SignManager {

    suspend fun sign(imei: String, sn: String, pubKey: String): SignDeviceResult

}
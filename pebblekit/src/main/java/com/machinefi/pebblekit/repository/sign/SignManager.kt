package com.machinefi.pebblekit.repository.sign

import com.machinefi.pebblekit.common.request.SignPebbleResult

internal interface SignManager {

    suspend fun sign(imei: String, sn: String, pubKey: String): SignPebbleResult

}

class SignException(msg: String): RuntimeException(msg)
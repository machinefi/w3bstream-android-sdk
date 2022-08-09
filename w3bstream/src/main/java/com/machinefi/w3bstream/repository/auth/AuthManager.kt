package com.machinefi.w3bstream.repository.auth

import com.machinefi.w3bstream.common.request.AuthResult

internal interface AuthManager {

    suspend fun authenticate(imei: String, sn: String, pubKey: String, signature: String): AuthResult

    suspend fun signData(data: ByteArray): String

    suspend fun getPublicKey(): String
}
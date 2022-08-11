package com.machinefi.w3bstream.utils.extension

import com.google.gson.Gson
import com.machinefi.w3bstream.repository.upload.HTTPS_SCHEMA
import com.machinefi.w3bstream.repository.upload.WEB_SOCKET_SCHEMA
import org.web3j.utils.Numeric

internal fun String.isValidJson(): Boolean {
    return try {
        Gson().fromJson(this, Any::class.java)
        true
    } catch (e: Exception) {
        false
    }
}

internal fun String.isValidServer(): Boolean {
    return this.startsWith(HTTPS_SCHEMA) || this.startsWith(WEB_SOCKET_SCHEMA)
}

internal fun String.cleanHexPrefix(): String = Numeric.cleanHexPrefix(this)

internal fun String.toHexByteArray(): ByteArray = Numeric.hexStringToByteArray(this)
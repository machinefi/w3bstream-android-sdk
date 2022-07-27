package com.machinefi.pebblekit.uitls.extension

import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import org.web3j.utils.Numeric

internal fun String.isJsonValid(): Boolean {
    return try {
        Gson().fromJson("", Any::class.java)
        true
    } catch (e: Exception) {
        false
    }
}

internal fun String.toast() {
    ToastUtils.showShort(this)
}

internal fun String.cleanHexPrefix(): String = Numeric.cleanHexPrefix(this)

internal fun String.toHexByteArray(): ByteArray = Numeric.hexStringToByteArray(this)
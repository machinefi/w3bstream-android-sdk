package com.machinefi.pebblekit.uitls.extension

import org.web3j.utils.Numeric

internal fun ByteArray.toHexString(): String {
    return Numeric.toHexString(this)
}
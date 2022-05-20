package io.iotex.pebble.utils.extension

import org.web3j.utils.Numeric

fun ByteArray.toHexString(): String {
    return Numeric.toHexString(this)
}



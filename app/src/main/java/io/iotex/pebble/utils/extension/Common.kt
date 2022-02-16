package io.iotex.pebble.utils.extension

import io.iotex.pebble.module.mqtt.Numeric

fun ByteArray.toHexString(): String {
    return Numeric.toHexString(this)
}



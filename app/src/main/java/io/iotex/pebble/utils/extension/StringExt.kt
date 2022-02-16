package io.iotex.pebble.utils.extension

import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ToastUtils
import io.iotex.pebble.module.mqtt.Numeric
import timber.log.Timber

fun String?.d(tag: String = "pebble d-->") {
    if (AppUtils.isAppDebug()) {
        this?.let {
            Timber.tag(tag).d(it)
        }
    }
}

fun String?.i(tag: String = "pebble i-->") {
    if (AppUtils.isAppDebug()) {
        this?.let {
            Timber.tag(tag).i(it)
        }
    }
}

fun String?.e(tag: String = "pebble e-->") {
    if (AppUtils.isAppDebug()) {
        this?.let {
            Timber.tag(tag).e(it)
        }
    }
}

fun String?.toast() {
    this?.let {
        ToastUtils.showShort(it)
    }
}

fun String.toHexByteArray(): ByteArray {
    return Numeric.hexStringToByteArray(this)
}

fun String.ellipsis(before: Int, after: Int): String {
    if (before > 0 && after > 0 && (before + after) < this.length)
        return this.substring(0, before) + "..." + this.substring(
            this.length - after,
            this.length
        )
    return this
}
package com.machinefi.metapebble.utils.extension

import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ToastUtils
import org.web3j.utils.Convert
import org.web3j.utils.Numeric
import timber.log.Timber
import java.math.BigDecimal

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

fun String.toWei(unit: Convert.Unit = Convert.Unit.ETHER): BigDecimal = Convert.toWei(this, unit)

fun String.cleanHexPrefix(): String = Numeric.cleanHexPrefix(this)

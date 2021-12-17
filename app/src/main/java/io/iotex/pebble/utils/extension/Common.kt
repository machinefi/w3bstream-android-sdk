package io.iotex.pebble.utils.extension

import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ToastUtils
import io.iotex.pebble.BuildConfig
import timber.log.Timber

//<editor-fold desc="String.Ext">
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
//</editor-fold>





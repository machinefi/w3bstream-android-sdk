package io.iotex.pebble.utils.extension

import com.blankj.utilcode.util.ConvertUtils
import com.drakeet.multitype.MultiTypeAdapter

fun Float.dp2px(): Int {
    return ConvertUtils.dp2px(this)
}

fun Int.dp2px(): Int {
    return ConvertUtils.dp2px(this.toFloat())
}

fun Float.px2dp(): Int {
    return ConvertUtils.px2dp(this)
}

fun Int.px2dp(): Int {
    return ConvertUtils.px2dp(this.toFloat())
}

fun <T> MultiTypeAdapter.updateItem(t: T, regex: (T) -> Boolean) {
    val oldList = this.items.toMutableList() as MutableList<T>
    val index = oldList.indexOfFirst {
        regex.invoke(it)
    }

    if (index != -1) {
        oldList[index] = t
    }

    this.items = oldList as List<Any>
    this.notifyItemChanged(index)
}



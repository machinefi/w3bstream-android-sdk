package io.iotex.pebble.utils.extension

import com.blankj.utilcode.util.ConvertUtils
import org.web3j.utils.Numeric
import java.math.BigDecimal
import java.math.RoundingMode

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

fun Double.formatDecimal(decimal: Int, mode: RoundingMode = RoundingMode.FLOOR): String {
    val bd = BigDecimal(this)
    return bd.setScale(decimal, mode).toPlainString()
}

fun BigDecimal.toHexString(): String {
    return Numeric.toHexStringWithPrefix(this.toBigInteger())
}
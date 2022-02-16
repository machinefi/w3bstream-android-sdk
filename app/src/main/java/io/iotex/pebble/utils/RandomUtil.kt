package io.iotex.pebble.utils

import java.util.*

object RandomUtil {
    const val ALLCHAR = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    const val LETTERCHAR = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    const val NUMBERCHAR = "0123456789"

    /**
     * 生成制定范围内的随机数
     *
     * @param scopeMin
     * @param scoeMax
     * @return
     */
    fun integer(scopeMin: Int, scoeMax: Int): Int {
        val random = Random()
        return random.nextInt(scoeMax) % (scoeMax - scopeMin + 1) + scopeMin
    }

    /**
     * 返回固定长度的数字
     *
     * @param length
     * @return
     */
    fun number(length: Int): String {
        val sb = StringBuffer()
        val random = Random()
        for (i in 0 until length) {
            sb.append(NUMBERCHAR[random.nextInt(NUMBERCHAR.length)])
        }
        return sb.toString()
    }

    /**
     * 返回一个定长的随机字符串(只包含大小写字母、数字)
     *
     * @param length 随机字符串长度
     * @return 随机字符串
     */
    fun String(length: Int): String {
        val sb = StringBuffer()
        val random = Random()
        for (i in 0 until length) {
            sb.append(ALLCHAR[random.nextInt(ALLCHAR.length)])
        }
        return sb.toString()
    }

    /**
     * 返回一个定长的随机纯字母字符串(只包含大小写字母)
     *
     * @param length 随机字符串长度
     * @return 随机字符串
     */
    fun MixString(length: Int): String {
        val sb = StringBuffer()
        val random = Random()
        for (i in 0 until length) {
            sb.append(ALLCHAR[random.nextInt(LETTERCHAR.length)])
        }
        return sb.toString()
    }

    /**
     * 返回一个定长的随机纯大写字母字符串(只包含大小写字母)
     *
     * @param length 随机字符串长度
     * @return 随机字符串
     */
    fun LowerString(length: Int): String {
        return MixString(length).toLowerCase()
    }

    /**
     * 返回一个定长的随机纯小写字母字符串(只包含大小写字母)
     *
     * @param length 随机字符串长度
     * @return 随机字符串
     */
    fun UpperString(length: Int): String {
        return MixString(length).toUpperCase()
    }
}
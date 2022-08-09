package com.machinefi.w3bstream.utils

import java.util.*

internal object RandomUtil {
    const val ALLCHAR = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    const val LETTERCHAR = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    const val NUMBERCHAR = "0123456789"

    /**
     * Generate random numbers within a specified range
     */
    fun integer(scopeMin: Int, scoeMax: Int): Int {
        val random = Random()
        return random.nextInt(scoeMax) % (scoeMax - scopeMin + 1) + scopeMin
    }

    /**
     * Returns a fixed length number
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
     * Returns a fixed-length random string (containing only uppercase and lowercase letters and numbers)
     */
    fun string(length: Int): String {
        val sb = StringBuffer()
        val random = Random()
        for (i in 0 until length) {
            sb.append(ALLCHAR[random.nextInt(ALLCHAR.length)])
        }
        return sb.toString()
    }

    /**
     * Returns a fixed-length random plain-alphabetic string (only uppercase and lowercase letters)
     */
    fun mixString(length: Int): String {
        val sb = StringBuffer()
        val random = Random()
        for (i in 0 until length) {
            sb.append(ALLCHAR[random.nextInt(LETTERCHAR.length)])
        }
        return sb.toString()
    }

    /**
     * Returns a fixed-length random pure uppercase string (containing only uppercase and lowercase letters)
     */
    fun lowerString(length: Int): String {
        return mixString(length).lowercase()
    }

    /**
     * Returns a fixed-length random pure lowercase string (containing only uppercase and lowercase letters)
     */
    fun upperString(length: Int): String {
        return mixString(length).uppercase()
    }
}
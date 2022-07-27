package com.machinefi.pebblekit.uitls

internal object EncryptUtil {

    fun setLength(msg: ByteArray, length: Int, right: Boolean = false): ByteArray {
        val result = ByteArray(length)
        val salt = ByteArray(length)
        if (right) {
            if (msg.size < length) {
                System.arraycopy(msg, 0, result, 0, msg.size)
                System.arraycopy(salt, 0, result, msg.size, salt.size - msg.size)
                return result
            }
            return msg
        } else {
            if (msg.size < length) {
                System.arraycopy(salt, 0, result, 0, salt.size - msg.size)
                System.arraycopy(msg, 0, result, salt.size - msg.size, msg.size)
                return result
            }
            return msg
        }
    }

    fun concat(vararg srcList: ByteArray): ByteArray {
        val length = srcList.map { it.size }.sum()
        val result = ByteArray(length)
        var cursor = 0
        srcList.forEach {
            System.arraycopy(it, 0, result, cursor, it.size)
            cursor += it.size
        }
        return result
    }


}
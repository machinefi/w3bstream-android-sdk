package io.iotex.pebble.utils

import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.EncryptUtils
import io.iotex.pebble.module.db.AppDatabase
import io.iotex.pebble.module.db.entries.DeviceEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigInteger

const val LEN_IMEI = 15
const val LEN_SN = 10

const val INTERVAL_SEND_DATA = 5

const val RETRY_COUNT = 3
const val RETRY_TIME = 2L

const val GPS_PRECISION = 100

object DeviceHelper {

    suspend fun createDevice(): DeviceEntry = withContext(Dispatchers.IO) {
        KeystoreUtil.createPk()
        val pubKey = KeystoreUtil.getPubKey()
        if (pubKey.isNullOrBlank()) {
            throw Exception("Failed to create MetaPebble")
        }

        DeviceEntry(imei(), sn(), pubKey, "").also {
            AppDatabase.mInstance.deviceDao().insertIfNonExist(it)
        }
    }

    private fun imei(): String {
//        val deviceId = DeviceUtils.getUniqueDeviceId()
//        val hash = EncryptUtils.encryptSHA256ToString(deviceId.toByteArray())
//        val imei = BigInteger(hash.substring(0, 10), 16).toString()
//        return "100${imei.substring(0, LEN_IMEI - 3)}"
        return "100${RandomUtil.number(LEN_IMEI - 3)}"
    }

    private fun sn(): String {
//        val deviceId = DeviceUtils.getAndroidID()
//        val hash = EncryptUtils.encryptSHA256ToString(deviceId.toByteArray())
//        return hash.substring(0, LEN_SN).uppercase()
        return RandomUtil.string(LEN_SN)
    }
}
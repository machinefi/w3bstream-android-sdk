package com.machinefi.metapebble.module.repository

import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.TimeUtils
import com.google.gson.Gson
import com.machinefi.metapebble.constant.SP_KEY_GPS_PRECISION
import com.machinefi.metapebble.constant.SP_KEY_SERVER_URL
import com.machinefi.metapebble.constant.URL_UPLOAD_DATA
import com.machinefi.metapebble.module.db.AppDatabase
import com.machinefi.metapebble.module.db.entries.RecordEntry
import com.machinefi.metapebble.module.manager.PebbleManager
import com.machinefi.metapebble.utils.GPSUtil
import com.machinefi.metapebble.utils.GPS_PRECISION
import com.machinefi.metapebble.utils.RandomUtil
import com.machinefi.metapebble.utils.extension.i
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.anko.doAsync
import java.io.Serializable
import java.math.BigDecimal
import javax.inject.Inject
import kotlin.math.log10

class UploadRepo @Inject constructor() {

    fun startUploadMetadata(imei: String) {
        val location = GPSUtil.getLocation() ?: return
        val url = SPUtils.getInstance().getString(SP_KEY_SERVER_URL, URL_UPLOAD_DATA)
        PebbleManager.pebbleKit.startUploading(url) {
            val gpsPrecision = SPUtils.getInstance().getInt(SP_KEY_GPS_PRECISION, GPS_PRECISION)
            val origin = 7 - log10(gpsPrecision.toDouble()).toInt()
            val decimal = if (origin >= 0) {
                origin
            } else {
                0
            }
//            val lat = GPSUtil.encodeLocation(location.latitude, decimal)
//            val long = GPSUtil.encodeLocation(location.longitude, decimal)
            val latP = RandomUtil.integer(0, 2)
            val longP = RandomUtil.integer(0, 2)
            val lat = RandomUtil.integer(0, 90)
            val long = RandomUtil.integer(0, 180)

            val latRandom = RandomUtil.integer(0, 9999999)
            val longRandom = RandomUtil.integer(0, 9999999)
            val latResult = if (latP == 0) {
                "-$lat$latRandom"
            } else {
                "$lat$latRandom"
            }
            val longResult = if (longP == 0) {
                "-$long$longRandom"
            } else {
                "$long$longRandom"
            }

            val random = RandomUtil.integer(10000, 99999)
            val bd = TimeUtils.getNowMills().toBigDecimal().div(BigDecimal.TEN.pow(3))
            val timestampStr = bd.setScale(0, BigDecimal.ROUND_DOWN)
            val sensorData = SensorData(
                1024,
                latResult,
                longResult,
                random.toString(),
                timestampStr.toLong()
            )

            doAsync {
                val rawLat = GPSUtil.decodeLocation(latResult.toLong())
                val rawLong = GPSUtil.decodeLocation(longResult.toLong())
                RecordEntry(imei, rawLat, rawLong, TimeUtils.getNowMills().toString()).also {
                    AppDatabase.mInstance.recordDao().insertIfNonExist(it)
                }
            }

            return@startUploading Gson().toJson(sensorData)
        }
    }

    fun stopUploadMetadata() {
        PebbleManager.pebbleKit.stopUploading()
    }

}

data class SensorData(
    val snr: Int,
    val latitude: String,
    val longitude: String,
    val random: String,
    val timestamp: Long
) : Serializable
package com.machinefi.metapebble.module.repository

import android.annotation.SuppressLint
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.TimeUtils
import com.google.gson.Gson
import com.iotex.pebble.utils.KeystoreUtil
import com.machinefi.metapebble.constant.*
import com.machinefi.metapebble.module.db.entries.DeviceEntry
import com.machinefi.metapebble.module.http.ApiService
import com.machinefi.metapebble.module.http.SensorData
import com.machinefi.metapebble.module.http.UploadDataBody
import com.machinefi.metapebble.module.mqtt.EncryptUtil
import com.machinefi.metapebble.utils.*
import com.machinefi.metapebble.utils.extension.i
import com.machinefi.metapebble.utils.extension.toHexByteArray
import com.machinefi.metapebble.utils.extension.toHexString
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.math.BigDecimal
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.log10

class UploadRepo @Inject constructor(val mApiService: ApiService) {

    private var mPollingComposite = CompositeDisposable()

    @SuppressLint("CheckResult")
    fun uploadMetadataViaHttps(imei: String) {
        polling {
            val url = SPUtils.getInstance().getString(SP_KEY_SERVER_URL, URL_UPLOAD_DATA)
            if (url.isNullOrBlank()) return@polling
            val body = encryptData(imei) ?: return@polling
            Gson().toJson(body).i()
            mApiService.uploadMetadata(url, body).compose(RxUtil.observableSchedulers()).subscribe()
        }
    }

    @SuppressLint("CheckResult")
    private fun polling(callback: () -> Unit) {
        if (!SPUtils.getInstance().getBoolean(SP_KEY_GPS_CHECKED, true)) return
        val interval = SPUtils.getInstance().getInt(SP_KEY_SUBMIT_FREQUENCY, INTERVAL_SEND_DATA)
        Observable.interval(0, interval.toLong(), TimeUnit.MINUTES)
            .doOnSubscribe {
                mPollingComposite.add(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                callback.invoke()
            }
    }

    private fun encryptData(imei: String): UploadDataBody? {
        val location = GPSUtil.getLocation() ?: return null
        val gpsPrecision = SPUtils.getInstance().getInt(SP_KEY_GPS_PRECISION, GPS_PRECISION)
        val origin = 7 - log10(gpsPrecision.toDouble()).toInt()
        val decimal = if (origin >= 0) {
            origin
        } else {
            0
        }
        val lat = GPSUtil.encodeLocation(location.latitude, decimal)
        val long = GPSUtil.encodeLocation(location.longitude, decimal)
        val bd = TimeUtils.getNowMills().toBigDecimal().div(BigDecimal.TEN.pow(3))
        val timestampStr = bd.setScale(0, BigDecimal.ROUND_DOWN)
        val timestampBytes = Integer.toHexString(timestampStr.toInt()).toHexByteArray()
        val random = RandomUtil.integer(10000, 99999)
        val sensorData = SensorData(
            1024,
            lat.toString(),
            long.toString(),
            random.toString())
        val dataByteArray = Gson().toJson(sensorData).toByteArray()
        val typeData = Integer.toHexString(0).toHexByteArray()
        val result = EncryptUtil.concat(
            EncryptUtil.setLength(typeData, 4),
            dataByteArray,
            EncryptUtil.setLength(timestampBytes, 4)
        )
        val signature = KeystoreUtil.signData(result).toHexByteArray()
        return UploadDataBody(
            imei,
            KeystoreUtil.getPubKey() ?: "",
            0,
            signature.toHexString(),
            timestampStr.toString(),
            sensorData
        )
    }

    fun uploadMetadata(device: DeviceEntry) {
        stopUploadMetadata()
        mPollingComposite = CompositeDisposable()
        uploadMetadataViaHttps(device.imei)
    }

    fun stopUploadMetadata() {
        mPollingComposite.dispose()
        mPollingComposite.clear()
    }

}
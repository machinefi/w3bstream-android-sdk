package com.machinefi.metapebble.module.repository

import SensorProtoData
import android.annotation.SuppressLint
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.TimeUtils
import com.iotex.pebble.utils.KeystoreUtil
import com.machinefi.metapebble.constant.SP_KEY_GPS_CHECKED
import com.machinefi.metapebble.constant.SP_KEY_GPS_PRECISION
import com.machinefi.metapebble.constant.SP_KEY_SUBMIT_FREQUENCY
import com.machinefi.metapebble.module.db.entries.DeviceEntry
import com.machinefi.metapebble.module.http.ApiService
import com.machinefi.metapebble.module.http.SensorData
import com.machinefi.metapebble.module.http.UploadDataBody
import com.machinefi.metapebble.module.mqtt.EncryptUtil
import com.machinefi.metapebble.module.mqtt.MqttHelper
import com.machinefi.metapebble.utils.*
import com.machinefi.metapebble.utils.extension.toHexByteArray
import com.machinefi.metapebble.utils.extension.toHexString
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import org.eclipse.paho.client.mqttv3.MqttException
import java.math.BigDecimal
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.log10

class UploadRepo @Inject constructor(val mApiService: ApiService) {

    private var mPollingComposite = CompositeDisposable()

    @SuppressLint("CheckResult")
    fun uploadMetadataViaHttps(imei: String) {
        polling {
//            val url = SPUtils.getInstance().getString(SP_KEY_SERVER_URL)
            val url = "https://trustream-http.onrender.com/api/data"
//            if (url.isNullOrBlank()) return@polling
//            val data = encryptData(imei)?.cleanHexPrefix() ?: return@polling
            val body = encryptData(imei) ?: return@polling
            mApiService.uploadMetadata(url, body).compose(RxUtil.observableSchedulers()).subscribe()
        }
    }

    @SuppressLint("CheckResult")
    fun uploadMetadataViaMqtt(device: DeviceEntry) {
        var tryCount = 0
        Observable.create<Unit> {
            if (MqttHelper.isConnect()) {
                MqttHelper.subscribe(device.imei)
                polling {
                    val data = encryptData(device.imei) ?: return@polling
//                    MqttHelper.publishData(device.imei, data.toHexByteArray())
                }
                it.onComplete()
            } else {
                it.onError(MqttException(MqttException.REASON_CODE_CLIENT_EXCEPTION.toInt()))
            }
        }
            .retryWhen {
                it.flatMap { err ->
                    if (tryCount++ < RETRY_COUNT) {
                        Observable.timer(RETRY_TIME, TimeUnit.MINUTES)
                    } else {
                        Observable.error(err)
                    }
                }
            }
            .compose(RxUtil.observableSchedulers())
            .subscribe({
            }, {
                it.printStackTrace()
            })
    }

    @SuppressLint("CheckResult")
    private fun polling(callback: () -> Unit) {
        if (!SPUtils.getInstance().getBoolean(SP_KEY_GPS_CHECKED, true)) return
        val interval = SPUtils.getInstance().getInt(SP_KEY_SUBMIT_FREQUENCY, INTERVAL_SEND_DATA)
        Observable.interval(0, interval.toLong(), TimeUnit.SECONDS)
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
//        val sensorData = SensorProtoData.SensorData.newBuilder()
//            .setLatitude(lat.toInt())
//            .setLongitude(long.toInt())
//            .setSnr(1024)
//            .setRandom(random.toString()).build().toByteArray()
        val sensorData = SensorData(
            1024,
            lat.toString(),
            long.toString(),
            random.toString())
        val dataByteArray = ConvertUtils.serializable2Bytes(sensorData)
        val typeData = Integer.toHexString(SensorProtoData.BinPackage.PackageType.DATA.number).toHexByteArray()
        val result = EncryptUtil.concat(
            EncryptUtil.setLength(typeData, 4),
            dataByteArray,
            EncryptUtil.setLength(timestampBytes, 4)
        )
        val signature = KeystoreUtil.signData(result).toHexByteArray()
        return UploadDataBody(
            imei,
            KeystoreUtil.getPubKey() ?: "",
            SensorProtoData.BinPackage.PackageType.DATA.number,
            signature.toHexString(),
            timestampStr.toString(),
            sensorData
        )

//        doAsync {
//            val record = RecordEntry(
//                imei,
//                lat.toString(),
//                long.toString(),
//                TimeUtils.getNowMills().toString()
//            )
//            AppDatabase.mInstance.recordDao()
//                .insertIfNonExist(record)
//        }
//
//        return data.toHexString()
    }

    fun uploadMetadata(device: DeviceEntry) {
        stopUploadMetadata()
        mPollingComposite = CompositeDisposable()
        uploadMetadataViaHttps(device.imei)
//        uploadMetadataViaMqtt(device)
    }

    fun stopUploadMetadata() {
        mPollingComposite.dispose()
        mPollingComposite.clear()
    }

}
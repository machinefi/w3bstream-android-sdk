package io.iotex.pebble.utils

import android.annotation.SuppressLint
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.TimeUtils
import io.iotex.pebble.constant.SP_KEY_GPS_CHECKED
import io.iotex.pebble.constant.SP_KEY_GPS_PRECISION
import io.iotex.pebble.constant.SP_KEY_SUBMIT_FREQUENCY
import io.iotex.pebble.module.db.AppDatabase
import io.iotex.pebble.module.db.entries.DeviceEntry
import io.iotex.pebble.module.db.entries.RecordEntry
import io.iotex.pebble.module.mqtt.EncryptUtil
import io.iotex.pebble.module.mqtt.MqttHelper
import io.iotex.pebble.utils.extension.formatDecimal
import io.iotex.pebble.utils.extension.i
import io.iotex.pebble.utils.extension.toast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.paho.client.mqttv3.MqttException
import org.jetbrains.anko.doAsync
import java.lang.Exception
import java.math.BigInteger
import java.util.concurrent.TimeUnit
import kotlin.math.log10

const val LEN_IMEI = 15
const val LEN_SN = 10

const val INTERVAL_SEND_DATA = 5

const val RETRY_COUNT = 3
const val RETRY_TIME = 2L

const val GPS_PRECISION = 100

object DeviceHelper {

    private var mPollingQueryDisposable: Disposable? = null
    private var mPollingSendDataDisposable: Disposable? = null

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
        val deviceId = DeviceUtils.getUniqueDeviceId()
        val hash = EncryptUtils.encryptSHA256ToString(deviceId.toByteArray())
        val imei = BigInteger(hash.substring(0, 10), 16).toString()
        return "100${imei.substring(0, LEN_IMEI - 3)}"
    }

    private fun sn(): String {
        val deviceId = DeviceUtils.getAndroidID()
        val hash = EncryptUtils.encryptSHA256ToString(deviceId.toByteArray())
        return hash.substring(0, LEN_SN).uppercase()
    }

    @SuppressLint("CheckResult")
    fun powerOn(device: DeviceEntry) {
        var tryCount = 0
        Observable.create<Unit> {
            if (MqttHelper.isConnect()) {
                MqttHelper.subscribe(device.imei)
                pollingSendData(device)
                it.onComplete()
            } else {
                it.onError(MqttException(MqttException.REASON_CODE_CLIENT_EXCEPTION.toInt()))
            }
        }
            .retryWhen {
                it.flatMap { err ->
                    if (tryCount++ < RETRY_COUNT) {
                        Observable.timer(RETRY_TIME, TimeUnit.SECONDS)
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

    fun powerOff(imei: String) {
        MqttHelper.unsubscribe(imei)
        stopQuerying()
        stopSendingData()
    }

    @SuppressLint("CheckResult")
    fun pollingSendData(device: DeviceEntry) {
        if (!SPUtils.getInstance().getBoolean(SP_KEY_GPS_CHECKED, true)) return
        mPollingSendDataDisposable?.dispose()
        val interval = SPUtils.getInstance().getInt(SP_KEY_SUBMIT_FREQUENCY, INTERVAL_SEND_DATA)
        mPollingSendDataDisposable =
            Observable.interval(0, interval.toLong(), TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val location = GPSUtil.getLocation()
                    location?.let { l ->
                        val gpsPrecision =
                            SPUtils.getInstance().getInt(SP_KEY_GPS_PRECISION, GPS_PRECISION)

                        val origin = 7 - log10(gpsPrecision.toDouble()).toInt()
                        val decimal = if (origin >= 0) {
                            origin
                        } else {
                            0
                        }

                        val lat = GPSUtil.encodeLocation(l.latitude, decimal)
                        val long = GPSUtil.encodeLocation(l.longitude, decimal)
                        val data = EncryptUtil.signMessage(device, lat, long)
                        MqttHelper.publishData(device.imei, data)
                        doAsync {
                            val record = RecordEntry(
                                device.imei,
                                l.latitude.formatDecimal(decimal),
                                l.longitude.formatDecimal(decimal),
                                TimeUtils.getNowMills().toString()
                            )
                            AppDatabase.mInstance.recordDao()
                                .insertIfNonExist(record)
                        }
                    }
                }
    }

    fun stopQuerying() {
        mPollingQueryDisposable?.dispose()
    }

    fun stopSendingData() {
        mPollingSendDataDisposable?.dispose()
    }
}
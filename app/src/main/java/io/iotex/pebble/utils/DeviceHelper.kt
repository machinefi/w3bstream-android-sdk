package io.iotex.pebble.utils

import android.annotation.SuppressLint
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.TimeUtils
import io.iotex.pebble.constant.SP_KEY_GPS_CHECKED
import io.iotex.pebble.constant.SP_KEY_GPS_PRECISION
import io.iotex.pebble.constant.SP_KEY_SUBMIT_FREQUENCY
import io.iotex.pebble.module.db.AppDatabase
import io.iotex.pebble.module.db.entries.DEVICE_STATUS_CONFIRM
import io.iotex.pebble.module.db.entries.DeviceEntry
import io.iotex.pebble.module.db.entries.RecordEntry
import io.iotex.pebble.module.keystore.Account
import io.iotex.pebble.module.keystore.KeystoreUtils
import io.iotex.pebble.module.mqtt.EncryptUtil
import io.iotex.pebble.module.mqtt.MqttHelper
import io.iotex.pebble.utils.extension.formatDecimal
import io.iotex.pebble.utils.extension.i
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.eclipse.paho.client.mqttv3.MqttException
import org.jetbrains.anko.doAsync
import java.math.BigDecimal
import java.util.concurrent.TimeUnit
import kotlin.math.log10

const val LEN_IMEI = 15
const val LEN_SN = 10

const val INTERVAL_QUERY = 5L
const val INTERVAL_SEND_DATA = 5

const val RETRY_COUNT = 3
const val RETRY_TIME = 2L

const val GPS_PRECISION = 100

object DeviceHelper {

    private var mPollingQueryDisposable: Disposable? = null
    private var mPollingSendDataDisposable: Disposable? = null

    fun createDevice(): DeviceEntry {
        val password = KeyStoreUtil.createRandomPassword()
        val encodedPassword = KeyStoreUtil.encodePassword(password)
        val account = Account.create()
        val keystoreFile = KeystoreUtils.createWalletFileByAccount(password, account)
        KeyStoreUtil.saveKeystoreFile(keystoreFile)
        val device =
            DeviceEntry(account.address(), "", imei(), sn(), encodedPassword, keystoreFile.id)
        AppDatabase.mInstance.deviceDao().insertIfNonExist(device)
        return device
    }

    fun imei(): String {
        return "100${RandomUtil.number(LEN_IMEI - 3)}"
    }

    fun sn(): String {
        return RandomUtil.String(LEN_SN)
    }

    @SuppressLint("CheckResult")
    fun powerOn(device: DeviceEntry) {
        var tryCount = 0
        Observable.create<Unit> {
            if (MqttHelper.isConnect()) {
                MqttHelper.subscribe(device.imei)
                pollingQuery(device)
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
    fun pollingQuery(device: DeviceEntry) {
        if (device.status != DEVICE_STATUS_CONFIRM) {
            mPollingQueryDisposable?.dispose()
            mPollingQueryDisposable = Observable.interval(0, INTERVAL_QUERY, TimeUnit.SECONDS)
                .subscribe {
                    MqttHelper.publishQuery(device.imei)
                    "pollingQuery".i()
                }
        } else {
            MqttHelper.publishQuery(device.imei)
        }
    }

    @SuppressLint("CheckResult")
    fun pollingSendData(device: DeviceEntry) {
        if (!SPUtils.getInstance().getBoolean(SP_KEY_GPS_CHECKED, true)) return
        if (device.status == DEVICE_STATUS_CONFIRM) {
            stopQuerying()
            mPollingSendDataDisposable?.dispose()
            val interval = SPUtils.getInstance().getInt(SP_KEY_SUBMIT_FREQUENCY, INTERVAL_SEND_DATA)
            mPollingSendDataDisposable =
                Observable.interval(0, interval.toLong(), TimeUnit.MINUTES)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        val location = GPSUtil.getLocation()
                        location?.let { l ->
                            val gpsPrecision = SPUtils.getInstance().getInt(SP_KEY_GPS_PRECISION, GPS_PRECISION)

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
                            "pollingSendData".i()
                            doAsync {
                                val record = RecordEntry(device.imei, l.latitude.formatDecimal(decimal),
                                    l.longitude.formatDecimal(decimal), TimeUtils.getNowMills().toString())
                                AppDatabase.mInstance.recordDao()
                                    .insertIfNonExist(record)
                            }
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
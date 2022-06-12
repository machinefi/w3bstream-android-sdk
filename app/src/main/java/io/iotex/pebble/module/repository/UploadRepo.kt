package io.iotex.pebble.module.repository

import android.annotation.SuppressLint
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.TimeUtils
import io.iotex.graphql.smartcontract.NftListQuery
import io.iotex.pebble.constant.*
import io.iotex.pebble.di.annocation.ApolloClientSmartContract
import io.iotex.pebble.module.db.AppDatabase
import io.iotex.pebble.module.db.entries.DeviceEntry
import io.iotex.pebble.module.db.entries.RecordEntry
import io.iotex.pebble.module.http.ApiService
import io.iotex.pebble.module.http.BaseResp
import io.iotex.pebble.module.http.SignPebbleResp
import io.iotex.pebble.module.http.UploadMetadataBody
import io.iotex.pebble.module.mqtt.EncryptUtil
import io.iotex.pebble.module.mqtt.MqttHelper
import io.iotex.pebble.module.walletconnect.WalletConnector
import io.iotex.pebble.utils.*
import io.iotex.pebble.utils.extension.formatDecimal
import io.iotex.pebble.utils.extension.toHexByteArray
import io.iotex.pebble.utils.extension.toHexString
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.paho.client.mqttv3.MqttException
import org.jetbrains.anko.doAsync
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.log10

class UploadRepo @Inject constructor(val mApiService: ApiService) {

    private val mPollingComposite = CompositeDisposable()

    @SuppressLint("CheckResult")
    suspend fun uploadMetadataViaHttps(imei: String) = withContext(Dispatchers.IO) {
        polling {
            val url = SPUtils.getInstance().getString(SP_KEY_SERVER_URL)
            if (url.isNullOrBlank()) return@polling
            val data = encryptData() ?: return@polling
            val body = UploadMetadataBody(imei, data)
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
                    val data = encryptData() ?: return@polling
                    MqttHelper.publishData(device.imei, data.toHexByteArray())
                }
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

    @SuppressLint("CheckResult")
    private fun polling(callback: () -> Unit) {
        if (!SPUtils.getInstance().getBoolean(SP_KEY_GPS_CHECKED, true)) return
        val interval = SPUtils.getInstance().getInt(SP_KEY_SUBMIT_FREQUENCY, INTERVAL_SEND_DATA)
        val disposable =
            Observable.interval(0, interval.toLong(), TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    callback.invoke()
                }
        mPollingComposite.add(disposable)
    }

    private fun encryptData(): String? {
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
        val data = EncryptUtil.signMessage(lat, long)
        return data.toHexString()
    }

    suspend fun uploadMetadata(device: DeviceEntry) {
        stopUploadMetadata()
        uploadMetadataViaHttps(device.imei)
        uploadMetadataViaMqtt(device)
    }

    fun stopUploadMetadata() {
        mPollingComposite.dispose()
        mPollingComposite.clear()
    }

}
package io.iotex.pebble.module.mqtt

import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.Utils
import io.iotex.pebble.R
import io.iotex.pebble.constant.UpdateDeviceEvent
import io.iotex.pebble.module.db.AppDatabase
import io.iotex.pebble.module.db.entries.DEVICE_STATUS_CONFIRM
import io.iotex.pebble.module.db.entries.DEVICE_STATUS_PROPOSE
import io.iotex.pebble.utils.extension.e
import io.iotex.pebble.utils.extension.i
import io.iotex.pebble.utils.extension.toast
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.doAsync
import org.json.JSONObject

const val SERVER_URI = "ssl://a11homvea4zo8t-ats.iot.us-east-1.amazonaws.com:8883"

object MqttHelper {

    private val mqttAndroidClient by lazy {
        MqttAndroidClient(Utils.getApp(), SERVER_URI, TimeUtils.getNowString())
    }

    private lateinit var mImei: String

    fun isConnect(): Boolean {
        return mqttAndroidClient.isConnected
    }

    fun connect() {
        mqttAndroidClient.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(reconnect: Boolean, serverURI: String) {
            }

            override fun connectionLost(cause: Throwable) {
                "connectionLost ${cause.message}".i()
            }

            @Throws(Exception::class)
            override fun messageArrived(topic: String, message: MqttMessage) {
                doAsync {
                    val body = String(message.payload)
                    "messageArrived $body".i()
                    val device = AppDatabase.mInstance.deviceDao().queryByImei(mImei) ?: return@doAsync
                    var needUpdate = false
                    val obj = JSONObject(body)
                    if (obj.has("status")) {
                        val status = obj.getInt("status")
                        if (device.status == DEVICE_STATUS_PROPOSE && status == DEVICE_STATUS_CONFIRM) {
                            "Success".toast()
                        }
                        needUpdate = device.status != status
                        device.status = status
                    }
                    if (obj.has("proposer")) {
                        val proposer = obj.getString("proposer")
                        needUpdate = (device.walletAddress != proposer).or(needUpdate)
                        device.walletAddress = proposer
                    }
                    "needUpdate ${needUpdate}".i()
                    if (needUpdate) {
                        AppDatabase.mInstance.deviceDao().update(device)
                        EventBus.getDefault().post(UpdateDeviceEvent(device))
                    }
                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {}
        })
        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.connectionTimeout = 3000
        mqttConnectOptions.keepAliveInterval = 1200
        mqttConnectOptions.isAutomaticReconnect = true
        mqttConnectOptions.isCleanSession = true

        val cert = Utils.getApp().resources.openRawResource(R.raw.cert)
        val key = Utils.getApp().resources.openRawResource(R.raw.key)
        val ca = Utils.getApp().resources.openRawResource(R.raw.ca)

        try {
            val socketFactory = EncryptUtil.getSocketFactory(ca, cert, key, "")
            mqttConnectOptions.socketFactory = socketFactory
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    "connect success".i()
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    exception.printStackTrace()
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun subscribe(imei: String) {
        mImei = imei
        try {
            val topicFilter = arrayOf("backend/${mImei}/status")
            val qos = intArrayOf(1)
            mqttAndroidClient.subscribe(topicFilter, qos, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    "subscribe success".i()
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    exception.message.e()
                }
            })
        } catch (ex: MqttException) {
            ex.printStackTrace()
        }
    }

    fun unsubscribe(imei: String) {
        mqttAndroidClient.unsubscribe("backend/${imei}/status")
    }

    fun publishMessage(topic: String, msg: ByteArray) {
        try {
            val message = MqttMessage()
            message.payload = msg
            mqttAndroidClient.publish(topic, message, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    "success:$msg".i()
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    "failed:$msg".i()
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun publishQuery(imei: String) {
        publishMessage("device/${imei}/query", ByteArray(0))
    }

    fun publishConfirm(imei: String, msg: ByteArray) {
        publishMessage("device/${imei}/confirm", msg)
    }

    fun publishData(imei: String, msg: ByteArray) {
        publishMessage("device/${imei}/data", msg)
    }

    fun disconnect() {
        mqttAndroidClient.disconnect()
    }

    fun release() {
        mqttAndroidClient.unregisterResources()
    }

}
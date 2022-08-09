package com.machinefi.sample.utils

import android.app.Service
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import android.os.Vibrator
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.Utils

object ShakeUtil {

    private val sensorManager by lazy {
        Utils.getApp().getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    private val shakeListener by lazy {
        ShakeSensorListener()
    }

    private var isShake = false
    private var shakeCallBack: (() -> Unit)? = null

    private class ShakeSensorListener : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (isShake) {
                return
            }
            val values = event.values
            val x = Math.abs(values[0])
            val y = Math.abs(values[1])
            val z = Math.abs(values[2])

            if (x > 19 || y > 19 || z > 19) {
                isShake = true
                vibrate(500)
                shakeCallBack?.invoke()
                Handler(Looper.getMainLooper())
                    .postDelayed({
                        isShake = false
                    }, 1000)
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    private fun vibrate(milliseconds: Long) {
        val vibrator = Utils.getApp().getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(milliseconds)
    }

    fun register(cb: () -> Unit) {
        sensorManager.registerListener(
            shakeListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_FASTEST
        )
        this.shakeCallBack = cb
    }

    fun unregister() {
        sensorManager.unregisterListener(shakeListener)
        shakeCallBack = null
    }
}
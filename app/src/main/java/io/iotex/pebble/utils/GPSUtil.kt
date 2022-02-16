package io.iotex.pebble.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.*
import android.os.Bundle
import com.blankj.utilcode.util.Utils
import io.iotex.pebble.utils.extension.e
import io.iotex.pebble.utils.extension.formatDecimal
import io.iotex.pebble.utils.extension.i
import java.math.BigDecimal
import java.util.*


object GPSUtil {
    private val mLocationListener: LocationListener = object : LocationListener {
        // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            "onStatusChanged".i()
        }

        // Provider被enable时触发此函数，比如GPS被打开
        override fun onProviderEnabled(provider: String) {
            "onProviderEnabled".i()
        }

        // Provider被disable时触发此函数，比如GPS被关闭
        override fun onProviderDisabled(provider: String) {
            "onProviderDisabled".i()
        }

        //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
        override fun onLocationChanged(location: Location) {}
    }

    /**
     * 获取地理位置，先根据GPS获取，再根据网络获取
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    fun getLocation(): Location? {
        var location: Location? = null
        try {
            val locationManager =
                Utils.getApp().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {  //从gps获取经纬度
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (location == null) { //当GPS信号弱没获取到位置的时候再从网络获取
                    location = getLocationByNetwork()
                }
            } else {    //从网络获取经纬度
                location = getLocationByNetwork()
            }
        } catch (e: Exception) {
            e.message?.e()
        }
        return location
    }

    /**
     * 判断是否开启了GPS或网络定位开关
     *
     * @return
     */
    fun isLocationProviderEnabled(): Boolean {
        var result = false
        val locationManager =
            Utils.getApp().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        ) {
            result = true
        }
        return result
    }

    /**
     * 获取地理位置，先根据GPS获取，再根据网络获取
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    private fun getLocationByNetwork(): Location? {
        var location: Location? = null
        val locationManager =
            Utils.getApp().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    1000,
                    0f,
                    mLocationListener
                )
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }
        } catch (e: Exception) {
            e.message?.e()
        }
        return location
    }

    fun getAddress(location: Location): List<Address> {
        var result: List<Address> = emptyList()
        try {
            val gc = Geocoder(Utils.getApp(), Locale.getDefault())
            result = gc.getFromLocation(
                location.latitude,
                location.longitude, 1
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return result
    }

    fun encodeLocation(value: Double, decimal: Int): Long {
        val v = value.formatDecimal(decimal)
        return BigDecimal(v).multiply(BigDecimal.TEN.pow(7)).toLong()
    }

}
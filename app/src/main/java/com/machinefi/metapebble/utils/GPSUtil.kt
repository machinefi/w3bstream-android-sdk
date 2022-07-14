package com.machinefi.metapebble.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.*
import android.os.Bundle
import android.util.Log
import com.blankj.utilcode.util.Utils
import com.machinefi.metapebble.utils.extension.e
import com.machinefi.metapebble.utils.extension.formatDecimal
import com.machinefi.metapebble.utils.extension.i
import java.math.BigDecimal
import java.util.*

object GPSUtil {
    private val mLocationListener: LocationListener = object : LocationListener {
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            "onStatusChanged".i()
        }

        override fun onProviderEnabled(provider: String) {
            "onProviderEnabled".i()
        }

        override fun onProviderDisabled(provider: String) {
            "onProviderDisabled".i()
        }

        override fun onLocationChanged(location: Location) {}
    }

    @SuppressLint("MissingPermission")
    fun getLocation(): Location? {
        var location: Location? = null
        try {
            val locationManager =
                Utils.getApp().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (location == null) {
                    location = getLocationByNetwork()
                }
            } else {
                location = getLocationByNetwork()
            }
        } catch (e: Exception) {
            e.message?.e()
        }
        return location
    }

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

    fun decodeLocation(value: Long): String {
        return BigDecimal(value).div(BigDecimal.TEN.pow(7)).toPlainString()
    }

}
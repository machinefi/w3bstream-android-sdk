package com.machinefi.sample.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.*
import android.os.Bundle
import com.blankj.utilcode.util.Utils
import java.util.*

object GPSUtil {
    private val mLocationListener: LocationListener = object : LocationListener {
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        }

        override fun onProviderEnabled(provider: String) {
        }

        override fun onProviderDisabled(provider: String) {
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
            e.printStackTrace()
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
            e.printStackTrace()
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
}
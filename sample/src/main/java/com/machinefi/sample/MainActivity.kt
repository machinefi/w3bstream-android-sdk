package com.machinefi.sample

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils
import com.machinefi.pebblekit.api.W3bstreamKit
import com.machinefi.pebblekit.api.W3bstreamKitConfig
import com.machinefi.pebblekit.repository.device.Device
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch
import org.json.JSONObject

const val AUTH_HOST = "Your host"
const val HTTPS_UPLOAD_API = "Your https server"
const val WEB_SOCKET_UPLOAD_API = "Your WebSocket server"

class MainActivity : AppCompatActivity() {

    private val config by lazy {
        W3bstreamKitConfig(
            AUTH_HOST,
            HTTPS_UPLOAD_API,
            WEB_SOCKET_UPLOAD_API
        )
    }

    private val w3bstreamKit by lazy {
        W3bstreamKit.Builder(config).build()
    }

    private lateinit var device: Device

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBtnCreate.setOnClickListener {
            create()
        }

        mBtnStartUpload.setOnClickListener {
            if (this::device.isInitialized) {
                uploadLocation(device.imei)
            }
        }

        mBtnStopUpload.setOnClickListener {
            w3bstreamKit.stopUploading()
        }
    }

    private fun create() {
        lifecycleScope.launch {
            device = w3bstreamKit.createDevice()
            mTvImei.text = "IMEI:${device.imei}"
            mTvSn.text = "SN:${device.sn}"
            mBtnCreate.visibility = View.GONE
            mBtnStartUpload.visibility = View.VISIBLE
            mBtnStopUpload.visibility = View.VISIBLE
        }
    }

    private fun uploadLocation(imei: String) {
        PermissionUtils
            .permission(PermissionConstants.LOCATION)
            .callback(object : PermissionUtils.SimpleCallback {
                override fun onGranted() {
                    uploadData(imei)
                }

                override fun onDenied() {
                }
            })
            .request()
    }

    private fun uploadData(imei: String) {
        w3bstreamKit.startUploading {
            val location = GPSUtil.getLocation()
            mTvLocation.text = "lat:${location?.latitude}  long:${location?.longitude}"
            val jsonObj = JSONObject()
            jsonObj.put("imei", imei)
            jsonObj.put("latitude", location?.latitude)
            jsonObj.put("location", location?.longitude)
            return@startUploading jsonObj.toString()
        }
    }

}
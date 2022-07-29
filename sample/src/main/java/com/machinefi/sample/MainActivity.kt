package com.machinefi.sample

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.TimeUtils
import com.machinefi.sample.utils.GPSUtil
import com.machinefi.sample.utils.RandomUtil
import com.machinefi.w3bstream.api.W3bStreamKit
import com.machinefi.w3bstream.api.W3bStreamKitConfig
import com.machinefi.w3bstream.repository.device.Device
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.math.BigDecimal

const val AUTH_HOST = "Your host"
const val HTTPS_UPLOAD_API = "Your https server"
const val WEB_SOCKET_UPLOAD_API = "Your WebSocket server"

class MainActivity : AppCompatActivity() {

    private val config by lazy {
        W3bStreamKitConfig(
            AUTH_HOST,
            HTTPS_UPLOAD_API,
            WEB_SOCKET_UPLOAD_API
        )
    }

    private val w3bStreamKit by lazy {
        W3bStreamKit.Builder(config).build()
    }

    private lateinit var device: Device

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mEtInterval.setText("300")
        mEtHttps.setText(HTTPS_UPLOAD_API)
        mEtWebSocket.setText(WEB_SOCKET_UPLOAD_API)
        mEtInterval.addTextChangedListener {
            val interval = mEtInterval.text.toString().toLong()
            w3bStreamKit.setUploadInterval(interval)
        }
        mEtHttps.addTextChangedListener {
            val httpsServer = mEtHttps.text.toString()
            w3bStreamKit.setHttpsServerApi(httpsServer)
        }
        mEtWebSocket.addTextChangedListener {
            val webSocketServer = mEtWebSocket.text.toString()
            w3bStreamKit.setWebSocketServerApi(webSocketServer)
        }

        mBtnCreate.setOnClickListener {
            create()
        }

        mBtnStartUpload.setOnClickListener {
            uploadLocation()
        }

        mBtnStopUpload.setOnClickListener {
            w3bStreamKit.stopUpload()
            mBtnStartUpload.visibility = View.VISIBLE
            mBtnStopUpload.visibility = View.GONE
        }
    }

    private fun create() {
        lifecycleScope.launch {
            device = w3bStreamKit.createDevice()
            mTvImei.text = "IMEI:${device.imei}"
            mTvSn.text = "SN:${device.sn}"
            mBtnCreate.visibility = View.GONE
            mClContent.visibility = View.VISIBLE
        }
    }

    private fun uploadLocation() {
        PermissionUtils
            .permission(PermissionConstants.LOCATION)
            .callback(object : PermissionUtils.SimpleCallback {
                override fun onGranted() {
                    uploadData()
                }

                override fun onDenied() {
                }
            })
            .request()
    }

    private fun uploadData() {
        mBtnStartUpload.visibility = View.GONE
        mBtnStopUpload.visibility = View.VISIBLE
        w3bStreamKit.startUpload {
            val location = GPSUtil.getLocation()
            mTvLocation.text = "latitude:${location?.latitude}  longitude:${location?.longitude}"
            val lat = BigDecimal(location?.latitude ?: 0.0).multiply(BigDecimal.TEN.pow(7)).toLong()
            val long = BigDecimal(location?.longitude ?: 0.0).multiply(BigDecimal.TEN.pow(7)).toLong()
            val random = RandomUtil.integer(10000, 99999)
            val timestamp = TimeUtils.getNowMills().toBigDecimal().div(BigDecimal.TEN.pow(3)).toLong()
            val jsonObj = JSONObject()
            jsonObj.put("snr", 1024)
            jsonObj.put("latitude", lat.toString())
            jsonObj.put("longitude", long.toString())
            jsonObj.put("random", random.toString())
            jsonObj.put("timestamp", timestamp)
            return@startUpload jsonObj.toString()
        }
    }

}
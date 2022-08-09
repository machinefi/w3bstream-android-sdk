package com.machinefi.sample

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.*
import com.machinefi.sample.utils.*
import com.machinefi.w3bstream.api.W3bStreamKit
import com.machinefi.w3bstream.api.W3bStreamKitConfig
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

const val SIGN_API= "Your sign api"
const val SERVER_API = "Your server api"
const val KEY_SHAKE_COUNT = "key_shake_count"
const val KEY_IMEI = "key_imei"
const val KEY_SN = "key_sn"
const val KEY_HISTORY = "key_history"
const val KEY_INTERVAL = "key_interval"
class MainActivity : AppCompatActivity() {

    private val config by lazy {
        W3bStreamKitConfig(
            SIGN_API,
            listOf(SERVER_API),
        )
    }

    private val w3bStreamKit by lazy {
        W3bStreamKit.Builder(config).build()
    }

    private var shakeCount = 0

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imei = SPUtils.getInstance().getString(KEY_IMEI)
        if (!imei.isNullOrBlank()) {
            mEtImei.setText(imei)
        }
        val sn = SPUtils.getInstance().getString(KEY_SN)
        if (!sn.isNullOrBlank()) {
            mEtSn.setText(sn)
        }

        val interval = SPUtils.getInstance().getLong(KEY_INTERVAL, 300)
        mEtInterval.setText(interval.toString())
        mEtServer.setText(SERVER_API)

        RxUtil.textChange(mEtInterval)
            .debounce(1000, TimeUnit.MILLISECONDS)
            .compose(RxUtil.observableSchedulers())
            .subscribe {
                kotlin.runCatching {
                    val interval = mEtInterval.text.toString().toLong()
                    SPUtils.getInstance().put(KEY_INTERVAL, interval)
                    w3bStreamKit.setUploadInterval(interval)
                }
            }

        RxUtil.textChange(mEtServer)
            .debounce(1000, TimeUnit.MILLISECONDS)
            .compose(RxUtil.observableSchedulers())
            .subscribe {
                val httpsServer = mEtServer.text.toString()
                w3bStreamKit.addServerApi(httpsServer)
            }

        mBtnStartUpload.setOnClickListener {
            val imei = mEtImei.text.toString().trim()
            if (imei.isBlank()) {
                ToastUtils.showShort("The imei is invalid")
                return@setOnClickListener
            }
            SPUtils.getInstance().put(KEY_IMEI, imei)
            val sn = mEtSn.text.toString().trim()
            if (sn.isBlank()) {
                ToastUtils.showShort("The sn is invalid")
                return@setOnClickListener
            }
            SPUtils.getInstance().put(KEY_SN, sn)
            uploadLocation()
        }

        mBtnStopUpload.setOnClickListener {
            w3bStreamKit.stopUpload()
            mBtnStartUpload.visibility = View.VISIBLE
            mBtnStopUpload.visibility = View.GONE
        }

        ShakeUtil.register {
            shakeCount++
            val old = SPUtils.getInstance().getInt(KEY_SHAKE_COUNT, 0)
            SPUtils.getInstance().put(KEY_SHAKE_COUNT, old + 1)
            mTvShakeCount.text = shakeCount.toString()
        }

        mTvHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
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
            val lat = BigDecimal(location?.latitude ?: 0.0).multiply(BigDecimal.TEN.pow(7)).toLong()
            val long = BigDecimal(location?.longitude ?: 0.0).multiply(BigDecimal.TEN.pow(7)).toLong()
            val random = RandomUtil.integer(10000, 99999)
            val timestamp = TimeUtils.getNowMills().toBigDecimal().div(BigDecimal.TEN.pow(3)).toLong()
//            val shakeCount = SPUtils.getInstance().getInt(KEY_SHAKE_COUNT, 0)
            val imei = SPUtils.getInstance().getString(KEY_IMEI)
            val jsonObj = JSONObject()
            jsonObj.put("snr", 1024)
            jsonObj.put("latitude", lat.toString())
            jsonObj.put("longitude", long.toString())
            jsonObj.put("random", random.toString())
            jsonObj.put("timestamp", timestamp)
            jsonObj.put("imei", imei)
            jsonObj.put("shakeCount", shakeCount)
            shakeCount = 0
            mTvShakeCount.text = shakeCount.toString()
//            SPUtils.getInstance().put(KEY_SHAKE_COUNT, 0)
            mTvTime.text = TimeUtils.getNowString()
            mJsonViewer.bindJson(jsonObj)
            val historyList = SPUtils.getInstance().getStringSet(KEY_HISTORY).toMutableList()
            historyList.add(jsonObj.toString())
            SPUtils.getInstance().put(KEY_HISTORY, historyList.toSet())
            return@startUpload jsonObj.toString()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ShakeUtil.unregister()
    }

}
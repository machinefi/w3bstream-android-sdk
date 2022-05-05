package io.iotex.pebble.app

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ColorUtils
import dagger.android.AndroidInjection
import io.iotex.pebble.R
import io.iotex.pebble.constant.PebbleStore
import io.iotex.pebble.module.db.entries.DeviceEntry
import io.iotex.pebble.utils.extension.i

const val STATE_KEY_DEVICE = "state_key_device"

class ActivityLifecycleCallback : ActivityLifecycleCallbacks {

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        BarUtils.setStatusBarLightMode(p0, false)
        BarUtils.setStatusBarColor(p0, ColorUtils.getColor(R.color.teal_800))

        val device = p1?.getSerializable(STATE_KEY_DEVICE) as? DeviceEntry
        device?.also {
            PebbleStore.setDevice(it)
        }
    }

    override fun onActivityStarted(activity: Activity) {
        fitStatusBar(activity)
    }

    private fun fitStatusBar(activity: Activity) {
        val placeholder = activity.findViewById<View>(R.id.mPlaceholder)
        if (placeholder != null) {
            val statusBarH = BarUtils.getStatusBarHeight()
            val params = placeholder.layoutParams
            params.height = statusBarH
            placeholder.layoutParams = params
        }

        val toolbar = activity.findViewById<Toolbar>(R.id.mPubToolbar)
        if (toolbar != null && activity is AppCompatActivity) {
            activity.setSupportActionBar(toolbar)
            activity.supportActionBar?.setDisplayShowTitleEnabled(false)
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            toolbar.setNavigationOnClickListener {
                activity.onBackPressed()
            }
        }

        activity.findViewById<TextView>(R.id.mTvTitle)?.text = activity.title
    }

    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        outState.putSerializable(STATE_KEY_DEVICE, PebbleStore.mDevice)
    }
    override fun onActivityDestroyed(activity: Activity) {}
}
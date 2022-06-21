package io.iotex.pebble.app

import android.content.Context
import androidx.multidex.MultiDex
import com.blankj.utilcode.util.AppUtils
import io.iotex.core.base.BaseApplication
import io.iotex.pebble.di.component.DaggerAppComponent
import io.iotex.pebble.module.mqtt.MqttHelper
import io.iotex.pebble.module.repository.AppRepo
import io.iotex.pebble.utils.extension.i
import io.reactivex.plugins.RxJavaPlugins
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class PebbleApp : BaseApplication() {

    @Inject
    lateinit var mAppRepo: AppRepo

    override fun onCreate() {
        super.onCreate()

        DaggerAppComponent.builder()
                .build()
                .inject(this)

        RxJavaPlugins.setErrorHandler { t: Throwable? ->
            Timber.e(t)
        }

        this.registerActivityLifecycleCallbacks(ActivityLifecycleCallback())

        if (AppUtils.isAppDebug()) {
            Timber.plant(Timber.DebugTree())
        }

        if (AppUtils.isAppForeground()) {
            MqttHelper.connect()
        }

        GlobalScope.launch {
            mAppRepo.queryContractsFromRemote()
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}
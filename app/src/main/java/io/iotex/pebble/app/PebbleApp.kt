package io.iotex.pebble.app

import android.content.Context
import androidx.multidex.MultiDex
import com.blankj.utilcode.util.AppUtils
import io.iotex.core.base.BaseApplication
import io.iotex.pebble.di.component.DaggerAppComponent
import io.iotex.pebble.module.mqtt.MqttHelper
import io.reactivex.plugins.RxJavaPlugins
import timber.log.Timber

class PebbleApp : BaseApplication() {

    init {
        System.loadLibrary("TrustWalletCore")

//        val bouncyCastleProvider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME)
//        if (bouncyCastleProvider != null) {
//            Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
//        }
//        Security.addProvider(BouncyCastleProvider())
    }

    override fun onCreate() {
        super.onCreate()

        DaggerAppComponent.builder()
                .build()
                .inject(this)

//        KeyStoreUtil.initKeyStore()

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
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}